package com.iomp.investment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.iomp.investment.dto.TransactionRequest;
import com.iomp.investment.enums.TransactionType;
import com.iomp.investment.service.RequestHashService;

@SpringBootTest
public class RequestHashServiceTest {
	
	@Autowired
    private RequestHashService requestHashService;
	
	@Test
	void createRequestHash() {
		
	}
	
	 @Test
	    void sameRequestShouldGenerateSameHash() {

	        TransactionRequest request1 = createRequest(
	                1L, 10L, TransactionType.BUY, "100");

	        TransactionRequest request2 = createRequest(
	                1L, 10L, TransactionType.BUY, "100");

	        String hash1 = requestHashService.generateHash(request1);
	        String hash2 = requestHashService.generateHash(request2);

	        assertEquals(hash1, hash2);
	    }

	    @Test
	    void differentQuantityShouldGenerateDifferentHash() {

	        TransactionRequest request1 = createRequest(
	                1L, 10L, TransactionType.BUY, "100");

	        TransactionRequest request2 = createRequest(
	                1L, 10L, TransactionType.BUY, "200");

	        String hash1 = requestHashService.generateHash(request1);
	        String hash2 = requestHashService.generateHash(request2);

	        assertNotEquals(hash1, hash2);
	    }

	    @Test
	    void differentSecurityShouldGenerateDifferentHash() {

	        TransactionRequest request1 = createRequest(
	                1L, 10L, TransactionType.BUY, "100");

	        TransactionRequest request2 = createRequest(
	                1L, 20L, TransactionType.BUY, "100");

	        String hash1 = requestHashService.generateHash(request1);
	        String hash2 = requestHashService.generateHash(request2);

	        assertNotEquals(hash1, hash2);
	    }

	    @Test
	    void differentTransactionTypeShouldGenerateDifferentHash() {

	        TransactionRequest request1 = createRequest(
	                1L, 10L, TransactionType.BUY, "100");

	        TransactionRequest request2 = createRequest(
	                1L, 10L, TransactionType.SELL, "100");

	        String hash1 = requestHashService.generateHash(request1);
	        String hash2 = requestHashService.generateHash(request2);

	        assertNotEquals(hash1, hash2);
	    }

	    @Test
	    void differentBigDecimalScaleShouldGenerateSameHash() {

	        TransactionRequest request1 = createRequest(
	                1L, 10L, TransactionType.BUY, "100");

	        TransactionRequest request2 = createRequest(
	                1L, 10L, TransactionType.BUY, "100.00");

	        String hash1 = requestHashService.generateHash(request1);
	        String hash2 = requestHashService.generateHash(request2);

	        assertEquals(hash1, hash2);
	    }
	    
	    @Test
	    void generatedHashShouldBe64Characters() {

	        TransactionRequest request = createRequest(
	                1L, 10L, TransactionType.BUY, "100");

	        String hash = requestHashService.generateHash(request);

	        assertEquals(64, hash.length());
	    }

	    private TransactionRequest createRequest(
	            Long portfolioId,
	            Long securityId,
	            TransactionType transactionType,
	            String quantity) {

	        TransactionRequest request = new TransactionRequest();

	        request.setPortfolioId(portfolioId);
	        request.setSecurityId(securityId);
	        request.setTransactionType(transactionType);
	        request.setQuantity(new BigDecimal(quantity));

	        return request;
	    }
	
	

}
