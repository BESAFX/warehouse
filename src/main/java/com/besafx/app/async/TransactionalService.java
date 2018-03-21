package com.besafx.app.async;

import com.besafx.app.entity.BillBuy;
import com.besafx.app.entity.Branch;
import com.besafx.app.entity.Offer;
import com.besafx.app.entity.Payment;
import com.besafx.app.service.BillBuyService;
import com.besafx.app.service.BranchService;
import com.besafx.app.service.OfferService;
import com.besafx.app.service.PaymentService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class TransactionalService {

    @Autowired
    private OfferService offerService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BillBuyService billBuyService;

    @Transactional
    public List<Offer> getOffersByDateAndBranch(Branch branch, Date dateFrom, Date dateTo) {
        return offerService.findByMasterBranchAndLastUpdateBetween(branch, dateFrom, dateTo);
    }

    @Transactional
    public List<Payment> getPaymentsByDateAndBranch(Branch branch, Date dateFrom, Date dateTo) {
        return paymentService.findByAccountCourseMasterBranchAndDateBetween(branch, dateFrom, dateTo);
    }

    @Transactional
    public List<BillBuy> getBillBuysByDateAndBranch(Branch branch, Date dateFrom, Date dateTo) {
        return billBuyService.findByBranchAndDateBetween(branch, dateFrom, dateTo);
    }

    @Transactional
    public List<Branch> getBranches(){
        return Lists.newArrayList(branchService.findAll());
    }
}
