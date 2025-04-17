package com.lokesh.parkinglot.repository;

import com.lokesh.parkinglot.bo.Invoice;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class InvoiceRepository implements IInvoiceRepository {

  private Map<String, Invoice> invoices = new HashMap<>();

  @Override
  public void saveInvoice(Invoice invoice) {
    invoices.put(invoice.getId(), invoice);
  }

  @Override
  public Invoice getInvoiceById(String invoiceId) {
    return invoices.get(invoiceId);
  }
}
