package com.lokesh.parkinglot.repository;

import com.lokesh.parkinglot.bo.Invoice;

public interface IInvoiceRepository {
  void saveInvoice(Invoice invoice);
  Invoice getInvoiceById(String invoiceId);
}
