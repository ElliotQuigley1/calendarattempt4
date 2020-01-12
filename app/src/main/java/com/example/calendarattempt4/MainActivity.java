package com.example.calendarattempt4;

import android.content.Intent;
import android.os.Bundle;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;
import java.util.Calendar;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    // Declares view objects
    private TextView Value;
    private TextView Data;
    private TextView score;
    private Button btngocalendar;
    // Initialises objects
    static int child_selected = 1;
    public Database db = new Database();
    static Statement s = null;
    static parent P = new parent(); // THIS LINE CREATES PARENT OBJECT BASED ON HARDCODED USERNAME PASSWORD AND ID
    // WILL BE MODIFIED TO GENUINE LOG IN, SEE PARENT() INSIDE PARENT.JAVA
    static child C = new child();
    static day D = new day();
    // Gets current date when user opens app
    static Date nowday = new Date();
    private String date_chosen;
    // Prepares values for graph plotting
    private int lastX = -10;


    private int togglers[] = {0,0,0,0,0,0,0,0};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(SaveSharedPreference.getUserName(MainActivity.this).length() == 0)
        {
            Intent intent = new Intent(MainActivity.this, Log_in.class);
            startActivity(intent);
        }
        else
        {
            // Stay at the current activity.
        }
        // Connects Database and displays current date data
        if (db.connect()) {
            s = db.getConnection();
            // Passes on Statement s to parent
            P.connect(s);
            try {
                P.login(SaveSharedPreference.getUserName(MainActivity.this),SaveSharedPreference.getPassword(MainActivity.this));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                date_chosen = sdf.format(nowday);
                // Passes on Statement s to child and selects CID depending on selected child
                selectChild_button(child_selected);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "CANNOT CONNECT to DATABASE", Toast.LENGTH_LONG).show();
        }


        // Initialises toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialises Navigation drawer
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_C2, R.id.nav_C3,
                R.id.nav_c_add, R.id.nav_logoff)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView username_display = header.findViewById(R.id.username_display);
        username_display.setText(P.getUsername() + " - Logged in");
        TextView email_display = header.findViewById(R.id.email_display);
        email_display.setText(P.getEmail());

        // Initialises graph view
        GraphView graph = (GraphView) findViewById(R.id.graph);



        TextView P_username = (TextView) findViewById(R.id.username_view);
        P_username.setText("Username: " + P.getUsername());

        // Customize graph viewport
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(28);
        viewport.setMinX(-10);
        viewport.setMaxX(0);
        viewport.setScrollable(true);
        viewport.setScalable(true);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Weeks ago");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Severity of Eczema (POEM)");
        // use static labels for horizontal and vertical labels
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"10","9","8","7","6","5","4","3","2","1","0"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        score = (TextView) findViewById(R.id.score_display);
        score.setText("POEM Scores for the past 10 weeks");


    }



    ///////////////////////////////////////////////////////////////////////////////////////////////
    // NOTE TO SELF: Code here to pass on add child / log-out from navigation drawer


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                //Toast.makeText(this, "Child 1", Toast.LENGTH_SHORT).show();
                child_selected = 1;
                try {
                    selectChild_button(child_selected);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nav_C2:
                //Toast.makeText(this, "Child 2", Toast.LENGTH_SHORT).show();
                child_selected = 2;
                try {
                    selectChild_button(child_selected);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nav_C3:
                //Toast.makeText(this, "Child 3", Toast.LENGTH_SHORT).show();
                child_selected = 3;
                try {
                    selectChild_button(child_selected);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nav_c_add:
                if(P.getChild_num() >= 3) {
                    Toast.makeText(this, "Only 3 children can be added! Delete one before adding!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, Child_info.class);
                    startActivity(intent);
                }
                break;
            case R.id.nav_logoff:
                SaveSharedPreference.setUserName(MainActivity.this,"0");
                Toast.makeText(this, "Logged off", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Log_in.class);
                startActivity(intent);
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // NOTE TO SELF: Create signup activity
    private void signup_button(String userName_from_app, String password_from_app, String email_from_app) throws SQLException {
        if (P.signup(userName_from_app, password_from_app, email_from_app) == false){
            Toast.makeText(this, "User already exists" + P.getEmail() + ", " + P.getUsername(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show();
            /*
            // Code
            Prompts makeChild_button activity to create child
             */
        }
    }

    // NOTE TO SELF: Create login activity
    public static boolean LogIn_button(String userName_from_app, String password_from_app) throws SQLException {

        //InputStream inputStream = new FileInputStream("src/main/resources/hhhhhhhhh.jpg"); //You can get an inputStream using any IO API

        //File outputfile = new File("src/main/resources/account.txt");
        //IO.write(image, "png", outputfile);

        return P.login(userName_from_app, password_from_app);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////













    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_delete_child:
                delete_child(child_selected);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Runs with child_num_from_app from onNavigationItemSelected() to connect DB with corresponding child_num
    public void selectChild_button(int child_num_from_app) throws SQLException {
        C.connect(s, P.getParent_ID(), child_num_from_app);
        Value = (TextView) findViewById(R.id.Value);
        Value.setText("Child name:\t\t\t" + "\nAge:\t\t\t" + "\nHeight (cm):\t\t\t" + "\nWeight (kg):\t\t\t");
        Data = (TextView) findViewById(R.id.Data);
        Data.setText(C.getName() + "\n" + C.getAge() + "\n" + C.getHeight() + "\n" + C.getWeight());


        // Gets child past POEM score and refreshes graphview
        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                try{
                    calendar.setTime(sdf.parse(date_chosen));
                } catch(ParseException e){
                    e.printStackTrace();
                }
                calendar.add(Calendar.DAY_OF_MONTH, -70);
                lastX = -10;
                GraphView graph = (GraphView) findViewById(R.id.graph);
                graph.removeAllSeries();
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
                graph.addSeries(series);
                // we add 10 new entries
                for (int i = 0; i < 10; i++) {
                    //start live plotting with current date
                    int runningTot = 0;
                    for (int j = 0; j <= 6; j++) { //for loop takes in 7 days and plots 1 datapoint
                        //should find a way to -1 to every date until 7 days and then plot 1 datapoint
                        runningTot = runningTot + get_answers();
                        calendar.add(Calendar.DAY_OF_MONTH, +1);
                        date_chosen = sdf.format(calendar.getTime());
                    }

                    final int final_value = runningTot;
                    series.appendData(new DataPoint(lastX++, final_value), false, 11);

                }
            }
        }).start();
    }


    // Deletes currently selected child
    private void delete_child(int child_selected) {
        if(P.getChild_num()>=1) {
            Intent intent = new Intent(MainActivity.this, Delete_child.class);
            startActivity(intent);
        } else {
            Toast.makeText(this,"No child" , Toast.LENGTH_SHORT).show();

        }

    }




    @Override
    protected void onResume() {
        super.onResume();
        // Set navigation drawer menu based on number of children of user
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        switch (P.getChild_num()) {
            case 0:
                menu.findItem(R.id.nav_home).setVisible(false);
            case 1:
                menu.findItem(R.id.nav_C2).setVisible(false);
            case 2:
                menu.findItem(R.id.nav_C3).setVisible(false);
                break;
            case 3:
        }

    }


    //Function to retrieve data from database and plot live
    public int get_answers() {
        int total_sum = 0;
        try {
            if (checkDay_on_state_change(date_chosen)) {
                // Converts 1 and 0 into int variable array
                for (int i = 0; i<MainActivity.D.answers.length(); i++) {
                    if(MainActivity.D.answers.charAt(i) == '1') {
                        togglers[i] = 1;
                    } else if (MainActivity.D.answers.charAt(i) == '0') {
                        togglers[i] = 0;
                    }
                }
            } else {
                for (int i = 0; i < togglers.length; i++){
                    togglers[i] = 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (int i = 0; i<togglers.length; i++) {
            total_sum = total_sum + togglers[i];
        }
        return total_sum/2;
    }




    // Passes on Statement s to day and retrieve data for selected day
    public static boolean checkDay_on_state_change(String date) throws SQLException {
        if (D.check(C.getChild_ID(), P.getParent_ID(), date, s)){
            // displays at editText on Android Studio
            return true;
        } else {
            return false;
        }
    }

    // Create answers for new date
    public static void makeDay(String newAnswers, String image) throws SQLException {
        D.create(newAnswers, image);
    }

    // Update existing answers
    public static void rewriteDay(String newAnswers, String image) throws SQLException {
        D.update(newAnswers, image);
    }

    // Sets button action
    public void calendar_button(View view) {
        Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
        startActivity(intent);
    }





    // Overrides for Navigation drawer
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);


        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }





    /*
        @RequiresApi(api = Build.VERSION_CODES.O)
    public int getScore()throws SQLException {
        int i=0;

        LocalDate date = java.time.LocalDate.now();
        String sqlStr = "SELECT record WHERE DID = \'"+date+"\';";
        ResultSet rset=s.executeQuery(sqlStr);
        while(rset.next()) {i=rset.getInt("DID");}
        return i;
    }


            // Sets calendar button
        btngocalendar = (Button) findViewById(R.id.btngocalendar);
        btngocalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });

     */













    //Image Upload and download
    /*
        // test upload image
        InputStream inputStream = new FileInputStream("src/main/resources/hhhhhhhhh.jpg"); //You can get an inputStream using any IO API
        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bytes = output.toByteArray();
        //String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
        String encodedString = Base64.getEncoder().encodeToString(bytes);

        String sqlStr = "insert into patients (familyname,givenname,phonenumber) values(\'"+encodedString+"\',1,1);";
        s.execute (sqlStr);


        // retrieve image
        String datastring = null;
        sqlStr = "SELECT familyname FROM patients WHERE phonenumber='1';";
        ResultSet rset = s.executeQuery(sqlStr);
        while (rset.next()) {
            datastring = rset.getString("familyname");
        }
        rset.close();


        try {
            byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(datastring);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            // write the image to a file
            File outputfile = new File("src/main/resources/myImage.png");
            ImageIO.write(image, "png", outputfile);

            Bitmap decodedByte = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);


        }catch(Exception e) {
            System.out.println(e.getStackTrace());
        }





        // test log in
        LogIn_button("D_username","D_password");


        //onStop
        s.close();*/

    // back up codes for reconstructing databases
    public void reconstruct_P () throws SQLException {
        try {
            String sqlStr = "create table parents (\n" +
                    "                          PID SERIAL PRIMARY KEY,\n" +
                    "                          username varchar(32) NOT NULL,\n" +
                    "                          password varchar(32) NOT NULL,\n" +
                    "                          email varchar(32) NOT NULL,\n" +
                    "                          Child_num varchar(32),\n" +
                    "                          Child_ID_1 varchar(32),\n" +
                    "                          Child_ID_2 varchar(32),\n" +
                    "                          Child_ID_3 varchar(32)\n" +
                    ");\n" +
                    "insert into parents (username,password,email,Child_num,Child_ID_1,Child_ID_2,Child_ID_3) values('D_username','D_password','D_email','3','1','2','3');";

            s.execute (sqlStr);
            sqlStr = "SELECT * FROM parents WHERE PID=1;";
            ResultSet rset = s.executeQuery(sqlStr);
            while (rset.next()) {
                System.out.println(rset.getInt("PID") + " " + rset.getString("username"));
            }
            rset.close();
        } catch (Exception e) {
            //String stackTrace = Log.getStackTraceString(e);
            //System.out.println(stackTrace);
        }
    }
    public static void reconstruct_C() throws SQLException {
        try {
            String sqlStr = "create table children (\n" +
                    "                          CID SERIAL PRIMARY KEY,\n" +
                    "                          Child_name varchar(32) NOT NULL,\n" +
                    "                          PID varchar(32) NOT NULL,\n" +
                    "                          Age varchar(32),\n" +
                    "                          Weight varchar(32),\n" +
                    "                          Height varchar(32),\n" +
                    ");\n" +
                    "insert into children (Child_name,PID,Age,Weight,Height) values('Fluffy','1','Puppy','13','40','143','1');";

            s.execute (sqlStr);
            sqlStr = "SELECT * FROM children WHERE CID=1;";
            ResultSet rset = s.executeQuery(sqlStr);
            while (rset.next()) {
                System.out.println(rset.getInt("CID"));
            }
            rset.close();
        } catch (Exception e) {
            //String stackTrace = Log.getStackTraceString(e);
            //System.out.println(stackTrace);
        }
    }
    public static void reconstruct_D() throws SQLException {
        try {
            String sqlStr = "create table dates (\n" +
                    "                          DID SERIAL PRIMARY KEY,\n" +
                    "                          CID varchar(32) NOT NULL,\n" +
                    "                          PID varchar(32) NOT NULL,\n" +
                    "                          date varchar(32) NOT NULL,\n" +
                    "                          Record varchar(32) NOT NULL\n" +
                    ");\n" +
                    "insert into dates (CID,PID,date,Record) values('1','1','2019-12-10','1101110110');";

            s.execute (sqlStr);
            sqlStr = "SELECT * FROM dates WHERE DID=1;";
            ResultSet rset = s.executeQuery(sqlStr);
            while (rset.next()) {
                System.out.println(rset.getInt("DID") + " " + rset.getString("Record"));
            }
            rset.close();
        } catch (Exception e) {
            System.out.println(e);

            //String stackTrace = Log.getStackTraceString(e);
            //System.out.println(stackTrace);
        }
    }


}


