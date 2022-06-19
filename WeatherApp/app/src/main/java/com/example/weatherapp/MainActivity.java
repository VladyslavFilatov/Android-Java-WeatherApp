package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText user_field;
    private Button main_btn;
    private TextView result_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_field = findViewById(R.id.user_field);
        main_btn = findViewById(R.id.main_btn);
        result_info = findViewById(R.id.result_info);


        main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_field.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG).show();
                else {
                    String city = user_field.getText().toString();
                    String key = "your-key";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric";

                    new GetURLDara().execute(url);

                }
            }
        });

    }


    @SuppressLint("StaticFieldLeak")
    private class GetURLDara extends AsyncTask<String, String, String>{

        @SuppressLint("SetTextI18n")
        protected  void onPreExecute() {
            super.onPreExecute();
            result_info.setText("Loading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null; // BufferedReader нужен чтобы детальнее считать всю информацию

            try {
                URL url = new URL(strings[0]); // создали обьет на основе которого можем обращаться в url адресу
                connection = (HttpURLConnection) url.openConnection(); // открываем само http соединение
                connection.connect(); // открываем само http соединение

                InputStream stream = connection.getInputStream(); // считываем данные из определенного потока - с нашего url
                reader = new BufferedReader(new InputStreamReader(stream)); // передаем поток в stream чтобы считаь верно данные с нашего url

                StringBuilder buffer = new StringBuilder();  // помещаем считаные данные в эту переменную
                String line = "";

                while ((line = reader.readLine()) != null) // line записываем обращение к reader и после обращение к методу readLine засчет которого мы считываем по одной линии и помещаем его в переменную Line, цикл длиться пока количество текста в стране не ровно null
                    buffer.append(line).append("\n"); // добовляем к нашей строке одну прочитаную линию и + переход на нову стороку

                return buffer.toString(); // возвращаем buffer и переобразуем его в строку

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally { // создаем постоянное условие чтобы закрывать соединение если оно автоматически не закрылось
                if (connection != null)
                    connection.disconnect();

                try {
                if (reader != null) // закрываем обьект для считывания данные если автоматически не закрылся
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null; // обязательно нужно что-то возвращать
        }

        @SuppressLint("SetTextI18n")
        @Override // создаем метод который будет срабатывать когда мы уже полностью получим все данные и мы можем показать данные клиенту
        protected  void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result); //создаем конструктор чтобы считать данные JSON и передаем туда параметр result которая содержить JSON
                result_info.setText("Weather: °С " + jsonObject.getJSONObject("main").getDouble("temp")); // отправляем данные уже на экран приложения
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
