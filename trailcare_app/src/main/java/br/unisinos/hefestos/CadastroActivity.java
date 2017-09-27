package br.unisinos.hefestos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import br.unisinos.hefestos.provider.HefestosContract;
import br.unisinos.hefestos.utility.ValidationUtility;
import br.unisinos.hefestos.webservice.SaveResourceTaskFinished;
import br.unisinos.hefestos.webservice.SaveWebserviceResourceTask;
import br.unisinos.hefestos.webservice.Webservice;

public class CadastroActivity extends AppCompatActivity {

    private static final String LOG_TAG = CadastroActivity.class.getSimpleName();
    private EditText mEdNome;
    private EditText mEditTextDescricao;
    private Spinner mSpinnerTipoRecurso;

    private Context mContext;

    private static Double mLastLongitude;
    private static Double mLastLatitude;

    private int mResourceType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        mContext = this;

        mEdNome = (EditText) findViewById(R.id.edNome);
        mEditTextDescricao = (EditText) findViewById(R.id.editTextDescricaoRecurso);
        mSpinnerTipoRecurso = (Spinner)findViewById(R.id.spinnerTipoRecurso);

        mSpinnerTipoRecurso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] resourceTypes = getResources().getStringArray(R.array.tipos_recursos_values);
                mResourceType = Integer.valueOf(resourceTypes[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void onClearButtonClicked(View view) {
        mEdNome.setText("");
        mEditTextDescricao.setText("");
        mSpinnerTipoRecurso.setSelection(0);
    }

    public void onSaveButtonClicked(View view) {
        if(validateFields()) {
            String nomeRecurso = mEdNome.getText().toString();
            if(nomeRecurso.length()>30){
                nomeRecurso = nomeRecurso.substring(0,29);
            }
            String descricaoRecurso = mEditTextDescricao.getText().toString();
            if(descricaoRecurso.length()>80){
                descricaoRecurso = descricaoRecurso.substring(0,79);
            }

            SharedPreferences sharedPreferences = getApplication().getSharedPreferences(HefestosContract.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            mLastLatitude = Double.longBitsToDouble(sharedPreferences.getLong(HefestosContract.LAST_LATITUDE, Double.doubleToLongBits(0)));
            mLastLongitude = Double.longBitsToDouble(sharedPreferences.getLong(HefestosContract.LAST_LONGITUDE, Double.doubleToLongBits(0)));

            SaveWebserviceResourceTask saveWebserviceResourceTask = new SaveWebserviceResourceTask(new SaveResourceTaskFinishedListener());
            saveWebserviceResourceTask.execute(Webservice.setNewResource(nomeRecurso, descricaoRecurso, mResourceType, mLastLatitude, mLastLongitude));

            Intent intent = new Intent(this,RecursosListaActivity.class);
            startActivity(intent);

        }else{
            Toast.makeText(this, R.string.invalid_fields, Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateFields(){
        boolean validNomeRecurso = ValidationUtility.validateString(mEdNome,mContext.getString(R.string.mandatory_field));
        boolean validDescricaoRecurso = ValidationUtility.validateString(mEditTextDescricao,mContext.getString(R.string.mandatory_field));

        boolean validResourceType = true;
        if(mResourceType == 0){
            TextView textView = (TextView) mSpinnerTipoRecurso.getSelectedView();
            textView.setError(mContext.getString(R.string.mandatory_field));
            validResourceType = false;
        }
        return (validNomeRecurso && validDescricaoRecurso && validResourceType);
    }

    public class SaveResourceTaskFinishedListener implements SaveResourceTaskFinished {
        @Override
        public void OnTaskFinished(boolean success) {
            if(success){
                Toast.makeText(mContext, R.string.resource_created_successfully, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(mContext, R.string.resource_not_created_successfully, Toast.LENGTH_LONG).show();
            }
        }
    }
}
