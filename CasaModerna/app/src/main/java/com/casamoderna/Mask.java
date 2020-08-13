package com.casamoderna;

/**
 * Created by daividvasconcelos on 04/07/2017.
 */

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;

public abstract class Mask {

    private static final String mask10 = "(##) ####-####";
    private static final String mask11 = "(##) #####-####";
    private static final String mask8 = "####-####";
    private static final String mask9 = "#####-####";

    private static final String maskCPF = "###.###.###-##";

    public static TextWatcher insert(final TextInputEditText editText) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = Mask.unmask(s.toString());
                String mask;
                String defaultMask = getDefaultMask(str);
                switch (str.length()) {
                    case 11:
                        mask = mask11;
                        break;
                    case 10:
                        mask = mask10;
                        break;
                    case 9:
                        mask = mask9;
                        break;
                    default:
                        mask = defaultMask;
                        break;
                }

                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : mask.toCharArray()) {
                    if ((m != '#' && str.length() > old.length()) || (m != '#' && str.length() < old.length() && str.length() != i)) {
                        mascara += m;
                        continue;
                    }

                    try {
                        mascara += str.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                editText.setText(mascara);
                editText.setSelection(mascara.length());
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };
    }

    private static String getDefaultMask(String str) {
        String defaultMask = mask8;
        if (str.length() > 11) {
            defaultMask = mask11;
        }
        return defaultMask;
    }

    public static String unmask(String s) {
        return s.replaceAll("[^0-9]*", "");
    }

    public static TextWatcher insert(final String mask, final EditText ediTxt) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String str = Mask.unmask(s.toString());
                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : mask.toCharArray()) {
                    if (m != '#' && str.length() > old.length()) {
                        mascara += m;
                        continue;
                    }
                    try {
                        mascara += str.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                ediTxt.setText(mascara);
                ediTxt.setSelection(mascara.length());
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };
    }


    public static TextWatcher insertCpf(final EditText editText) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = unmask(s.toString());
                String mask;
                String defaultMask = maskCPF;
                switch (str.length()) {
                    case 11:
                        mask = maskCPF;
                        break;


                    default:
                        mask = maskCPF;
                        break;
                }

                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : mask.toCharArray()) {
                    if ((m != '#' && str.length() > old.length()) || (m != '#' && str.length() < old.length() && str.length() != i)) {
                        mascara += m;
                        continue;
                    }

                    try {
                        mascara += str.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                editText.setText(mascara);
                editText.setSelection(mascara.length());
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        };
    }


}
