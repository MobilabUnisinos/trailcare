package br.unisinos.hefestos.utility;

import android.widget.EditText;

public class ValidationUtility {

    public static boolean validateString(EditText editText, String message){
        if(editText.getText() == null){
            editText.setError(message);
            return false;
        }
        String value = editText.getText().toString();
        if ("".equals(value.trim())) {
            editText.setError(message);
            return false;
        }
        return true;
    }
}
