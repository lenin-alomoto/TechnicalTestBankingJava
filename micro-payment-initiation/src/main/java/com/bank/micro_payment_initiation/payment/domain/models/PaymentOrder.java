package com.bank.micro_payment_initiation.payment.domain.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class PaymentOrder {
	private UUID paymentOrderId;
	private String externalId;
	private BankAccount debtorAccount;
	private BankAccount creditorAccount;
	private PaymentAmount amount;
	private String remittanceInfo;
	private LocalDate requestedExecutionDate;
	private PaymentOrderStatus status;
	private LocalDateTime creationTimestamp;
	private LocalDateTime lastUpdateTimestamp;

	public PaymentOrder() {
	}

	public PaymentOrder(UUID paymentOrderId, String externalId, BankAccount debtorAccount, BankAccount creditorAccount,
			PaymentAmount amount, String remittanceInfo, LocalDate requestedExecutionDate, PaymentOrderStatus status,
			LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp) {
		this.paymentOrderId = paymentOrderId;
		this.externalId = externalId;
		this.debtorAccount = debtorAccount;
		this.creditorAccount = creditorAccount;
		this.amount = amount;
		this.remittanceInfo = remittanceInfo;
		this.requestedExecutionDate = requestedExecutionDate;
		this.status = status;
		this.creationTimestamp = creationTimestamp;
		this.lastUpdateTimestamp = lastUpdateTimestamp;
	}

	public void validateForInitiation() {
		if (externalId == null || externalId.isBlank()) {
			throw new IllegalArgumentException("externalId is required");
		}
		if (debtorAccount == null) {
			throw new IllegalArgumentException("debtorAccount is required");
		}
		if (creditorAccount == null) {
			throw new IllegalArgumentException("creditorAccount is required");
		}
		if (amount == null) {
			throw new IllegalArgumentException("amount is required");
		}
		amount.validate();
		debtorAccount.validate();
		creditorAccount.validate();

		if (requestedExecutionDate == null) {
			throw new IllegalArgumentException("requestedExecutionDate is required");
		}
	}

	public void markAsReceived() {
		this.status = PaymentOrderStatus.RECEIVED;
		touchLastUpdate();
	}

	public void markAsAccepted() {
		this.status = PaymentOrderStatus.ACCEPTED;
		touchLastUpdate();
	}

	public void markAsExecuted() {
		this.status = PaymentOrderStatus.EXECUTED;
		touchLastUpdate();
	}

	public void markAsRejected() {
		this.status = PaymentOrderStatus.REJECTED;
		touchLastUpdate();
	}

	public void initializeTimestampsIfNeeded() {
		LocalDateTime now = LocalDateTime.now();
		if (creationTimestamp == null) {
			creationTimestamp = now;
		}
		if (lastUpdateTimestamp == null) {
			lastUpdateTimestamp = now;
		}
	}

	private void touchLastUpdate() {
	    this.lastUpdateTimestamp = LocalDateTime.now().plusNanos(1);
	}

	public UUID getPaymentOrderId() {
		return paymentOrderId;
	}

	public void setPaymentOrderId(UUID paymentOrderId) {
		this.paymentOrderId = paymentOrderId;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public BankAccount getDebtorAccount() {
		return debtorAccount;
	}

	public void setDebtorAccount(BankAccount debtorAccount) {
		this.debtorAccount = debtorAccount;
	}

	public BankAccount getCreditorAccount() {
		return creditorAccount;
	}

	public void setCreditorAccount(BankAccount creditorAccount) {
		this.creditorAccount = creditorAccount;
	}

	public PaymentAmount getAmount() {
		return amount;
	}

	public void setAmount(PaymentAmount amount) {
		this.amount = amount;
	}

	public String getRemittanceInfo() {
		return remittanceInfo;
	}

	public void setRemittanceInfo(String remittanceInfo) {
		this.remittanceInfo = remittanceInfo;
	}

	public LocalDate getRequestedExecutionDate() {
		return requestedExecutionDate;
	}

	public void setRequestedExecutionDate(LocalDate requestedExecutionDate) {
		this.requestedExecutionDate = requestedExecutionDate;
	}

	public PaymentOrderStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentOrderStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(LocalDateTime creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public LocalDateTime getLastUpdateTimestamp() {
		return lastUpdateTimestamp;
	}

	public void setLastUpdateTimestamp(LocalDateTime lastUpdateTimestamp) {
		this.lastUpdateTimestamp = lastUpdateTimestamp;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof PaymentOrder))
			return false;
		PaymentOrder that = (PaymentOrder) o;
		return Objects.equals(paymentOrderId, that.paymentOrderId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(paymentOrderId);
	}
}
