package com.waterworks.mlqs.etl.infra.mongoin.dto;

import com.waterworks.mlqs.etl.app.suppliers.domain.Supplier;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("product_catalog_creation_supplier")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SupplierDocumentDTO {
  private String supplierId;
  private String name;
  private String address;
  private String email;
  private String phoneNumber;
  private LocalDateTime creationDate;

  public Supplier convertToSupplier() {
    return Supplier.builder()
        .supplierId(this.supplierId)
        .name(this.name)
        .address(this.address)
        .email(this.email)
        .phoneNumber(this.phoneNumber)
        .creationDate(this.creationDate)
        .build();
  }
}
