package rkhodi.huddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //get the data that was passed to this activity
        ArrayList<String> directions = (ArrayList<String>) getIntent().getSerializableExtra("directions");

        LinearLayout tempLayout= (LinearLayout) findViewById(R.id.mainLayout);
        List<TextView> txtList = new ArrayList<TextView>();

        //credit for help for this module can be found at:
        //http://stackoverflow.com/questions/4576848/creating-linear-layout-with-textviews-using-a-for-loop
        //http://stackoverflow.com/questions/7334751/how-to-add-multiple-textview-dynamicall-to-the-define-linearlayout-in-main-xml
        for(int i = 0; i < directions.size(); i++)
        {
            TextView dir = new TextView(getApplicationContext());
            dir.setText(removeHTML(directions.get(i)) + "\n");
            dir.setTextColor(0xFFFF0000);
            tempLayout.addView(dir);
            txtList.add(dir);
        }
    }

    //write a funcion to remove the html tags from a string
    //input: String
    //return: String
    String removeHTML(String s)
    {
        String builder = "";
        Boolean isLeft = true;
        for(int i = 0; i < s.length(); i++)
        {
            if(s.charAt(i) == '<' && isLeft)
            {
                i = i + 2;
                isLeft = false;
            }
            else if(s.charAt(i) == '<' && !isLeft)
            {
                i = i + 3;
                isLeft = true;
            }
            else
            {
                builder = builder + s.charAt(i);
            }
        }
        return builder;
    }
}
