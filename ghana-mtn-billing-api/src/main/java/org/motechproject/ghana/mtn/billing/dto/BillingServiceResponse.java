package org.motechproject.ghana.mtn.billing.dto;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.ghana.mtn.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

public class BillingServiceResponse<T> {

   private T value;
   private List<ValidationError> validationErrors;

   public BillingServiceResponse(T value) {
       this();
       this.value = value;
   }

   public BillingServiceResponse() {
       validationErrors = new ArrayList<ValidationError>();
   }

   public T getValue() {
       return value;
   }

   public boolean hasErrors() {
       return !CollectionUtils.isEmpty(validationErrors);
   }

   public List<ValidationError> getValidationErrors() {
       return validationErrors;
   }

   public void addError(ValidationError validationError) {
       validationErrors.add(validationError);
   }
}