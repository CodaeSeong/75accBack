package com.project.Accounting.acc.slip.service;

import com.project.Accounting.acc.entity.menu.Slip;
import com.project.Accounting.acc.entity.menu.journal.Journal;
import com.project.Accounting.acc.entity.menu.journal.JournalDetail;
import com.project.Accounting.acc.entity.menu.journal.JournalDetailId;
import com.project.Accounting.acc.slip.dto.SlipDTO;
import com.project.Accounting.acc.slip.repository.SlipRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Data
@Transactional
public class SlipServiceFacadeImpl implements SlipServiceFacade {

    @Autowired
    private final SlipRepository slipRepository;

    @Autowired
    private final JournalDetailService journalDetailService;

    @Autowired
    private final EntityManager entityManager;



    @Override
    public List<Slip> getSlipList() {
        List<Slip> slipList = slipRepository.findAllFetch();


        return slipList;
    }

    @Override
    public List<SlipDTO> getOnlySlipList() {
        List<SlipDTO> onlySlipList = slipRepository.findOnlySlipList();
        return onlySlipList;
    }


    @Override
    public Optional<Slip> getSlip(String slipId) {
        Optional<Slip> slip = slipRepository.findById(slipId);
        System.out.println("slip.get() = " + slip.get());

        return slip;
    }

    @Override
    public String registerSlip(Slip slipForm) {

        StringBuffer slipNo= new StringBuffer();

        String count = slipRepository.countSlipByReportingDate(slipForm.getReportingDate())+1 + "";
        slipNo.append(slipForm.getReportingDate().replace("-",""));
        slipNo.append("SLIP");
        String countFormat = String.format("%05d",Integer.parseInt(count));
        slipNo.append(countFormat);
        String slipNoResult = slipNo.toString();

        slipForm.setId(slipNoResult);


        slipForm.getJournals().forEach( journal -> {
            StringBuffer journalNo = new StringBuffer();
            journalNo.append(slipNoResult);
            journalNo.append("JOURNAL");
            journalNo.append(journal.getId());
            Slip slip = new Slip();
            slip.setId(slipNoResult);
            journal.setSlip(slip);
            journal.setId(journalNo.toString());


            JournalDetail journalDetail = journal.getJournalDetail();
            journalDetail.setId(new JournalDetailId());

            Journal journal1 = new Journal();
            journal1.setId(journalNo.toString());

            journalDetail.getId().setJournal(journal1);
            String sql = "SELECT JOURNAL_DETAIL_NO.NEXTVAL FROM dual";
            Query query = entityManager.createNativeQuery(sql);
            Object singleResult = query.getSingleResult();

            journalDetail.getId().setId(Long.parseLong(singleResult.toString()));


            JournalDetail regiser = journalDetailService.regiser(journalDetail);
            System.out.println("regiser = " + regiser);
            journal.setJournalDetail(regiser);


        });



        slipRepository.save(slipForm);

        System.out.println("Journal 출력"+slipForm.getJournals());

        return slipNoResult;
    }

    @Override
    public int findTodayslipsCount(String date){

        int count = slipRepository.countSlipByReportingDate("2022-09-20");

        return count;
    }


}