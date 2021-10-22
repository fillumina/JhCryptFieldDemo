package com.fillumina.demo.jhcryptfield.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.fillumina.demo.jhcryptfield.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CustomerAddressTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomerAddress.class);
        CustomerAddress customerAddress1 = new CustomerAddress();
        customerAddress1.setId(1L);
        CustomerAddress customerAddress2 = new CustomerAddress();
        customerAddress2.setId(customerAddress1.getId());
        assertThat(customerAddress1).isEqualTo(customerAddress2);
        customerAddress2.setId(2L);
        assertThat(customerAddress1).isNotEqualTo(customerAddress2);
        customerAddress1.setId(null);
        assertThat(customerAddress1).isNotEqualTo(customerAddress2);
    }
}
