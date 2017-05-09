package rkhodi.huddy;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    Button goButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //make a listener for the go button
        goButton = (Button) findViewById(R.id.goButton);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText start = (EditText)findViewById(R.id.Start);
                EditText end = (EditText)findViewById(R.id.Dest);

                //get the values from the start and end editText
                //remove the whitespace
                String startPoint = start.getText().toString();
                startPoint = startPoint.replaceAll("\\s+","");
                String endPoint = end.getText().toString();
                endPoint = endPoint.replaceAll("\\s+","");

                //call the asyncTask to make an API call
                RetrieveFeedTask task = new RetrieveFeedTask();
                task.execute(startPoint, endPoint);
                ArrayList<String> directions = null;
                //get the return value of the asyncTast
                //which is an ArrayList of the directions
                try {
                    directions = task.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                //pass the directions to the new activity and start it
                Intent intent = new Intent(getBaseContext(), Main2Activity.class);
                intent.putExtra("directions", directions);
                startActivity(intent);
            }
        });
    }

    //credit/help for asyncTask can be found at Google's own documentation
    class RetrieveFeedTask extends AsyncTask<String, Void, ArrayList<String>> {

        private Exception exception;

        protected ArrayList<String> doInBackground(String... params) {
            try {
                ArrayList<String> directions = new ArrayList<String>();

                //make the API call
                String accessToken = "AIzaSyA4P7Kf40a6doZOxc6XwTBttElDqWFtZMk";
                try {
                    URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=" + params[0] + "&destination=" + params[1] + "&key=" + accessToken);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        //read the input line by line and make a string builder
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        //convert the JSON object into a string
                        String temp = stringBuilder.toString();
                        //use the getTurns function to return the directions from the JSON object
                        return getTurns(temp);
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                    return null;
                }
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

    }

    public ArrayList<String> getTurns(String jsonString) throws JSONException {
        JSONObject obj = new JSONObject(jsonString);
        JSONArray steps = obj.getJSONArray("routes");
        ArrayList<String> returnSteps = new ArrayList<String>();

        //parse the JSON string and find the relevant information
        //in this case it is in the steps array
        JSONObject objAtIndex =  steps.optJSONObject(0);
        JSONArray temps = objAtIndex.getJSONArray("legs");
        JSONObject anotherTemp =  temps.optJSONObject(0);
        JSONArray stepsArr = anotherTemp.getJSONArray("steps");
        for(int k = 0; k < stepsArr.length(); k++)
        {
            //add all the steps to a temp array to be returned
            JSONObject anotherOne =  stepsArr.optJSONObject(k);
            String instruction = anotherOne.getString("html_instructions");
            returnSteps.add(instruction);
        }

        //return the steps array
        return returnSteps;
    }
}

//https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood4&key=AIzaSyA4P7Kf40a6doZOxc6XwTBttElDqWFtZMk