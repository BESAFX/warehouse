package com.besafx.app.entity.wrapper;

import com.besafx.app.entity.Payment;
import lombok.Data;

import java.io.File;

@Data
public class PaymentAttachWrapper {
    private Payment payment;
    private File file;
}
