package com.lumiere.app.domain;

import static com.lumiere.app.domain.VoucherTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VoucherTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Voucher.class);
        Voucher voucher1 = getVoucherSample1();
        Voucher voucher2 = new Voucher();
        assertThat(voucher1).isNotEqualTo(voucher2);

        voucher2.setId(voucher1.getId());
        assertThat(voucher1).isEqualTo(voucher2);

        voucher2 = getVoucherSample2();
        assertThat(voucher1).isNotEqualTo(voucher2);
    }
}
