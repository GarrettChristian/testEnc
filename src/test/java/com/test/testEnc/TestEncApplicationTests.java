package com.test.testEnc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Time;
import java.util.Timer;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestEncApplicationTests {

	@Test
	public void entityClientTest() {
		AesEncryption aesEncryption = new AesEncryption();
		try {

			Long start = System.currentTimeMillis();
			String secretKeY = aesEncryption.encyrpt("PlEaSeEnCrYpT");
			System.out.println("encrypted: " + secretKeY);
			System.out.println("encryt time: " + (System.currentTimeMillis() - start));
			assert ("PlEaSeEnCrYpT".equals(aesEncryption.decrypt(secretKeY)));
			System.out.println("Finished dycrypting:" + (System.currentTimeMillis() - start));

		} catch (Exception e) {
			assert (false);
		}

	}

}
