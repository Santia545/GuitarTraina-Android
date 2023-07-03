package com.example.guitartraina.activities.account;

import com.google.android.gms.wallet.WalletConstants;

import java.util.Arrays;
import java.util.List;
public class Constants {
    //Modo de prueba para Gpay
    public static final int PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST;

    /**
     * The allowed networks to be requested from the API. If the user has cards from networks not
     * specified here in their account, these will not be offered for them to choose in the popup.
     */
    public static final List<String> SUPPORTED_NETWORKS = Arrays.asList(
            "AMEX",
            "DISCOVER",
            "JCB",
            "MASTERCARD",
            "VISA");

    /**
     * The Google Pay API may return cards on file on Google.com (PAN_ONLY) and/or a device token on
     * an Android device authenticated with a 3-D Secure cryptogram (CRYPTOGRAM_3DS).
     */
    public static final List<String> SUPPORTED_METHODS = Arrays.asList(
            "PAN_ONLY",
            "CRYPTOGRAM_3DS");

    /**
     * Required by the API, but not visible to the user.
     */
    public static final String COUNTRY_CODE = "US";

    /**
     * Required by the API, but not visible to the user.
     */
    public static final String CURRENCY_CODE = "USD";


}