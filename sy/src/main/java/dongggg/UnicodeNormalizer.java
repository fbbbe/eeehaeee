package dongggg;

import java.text.Normalizer;

public final class UnicodeNormalizer {

    private UnicodeNormalizer() {
        // 유틸 클래스라서 생성 못 하게 막음
    }

    /** null-safe NFC 정규화 */
    public static String nfc(String s) {
        if (s == null)
            return null;
        return Normalizer.normalize(s, Normalizer.Form.NFC);
    }
}