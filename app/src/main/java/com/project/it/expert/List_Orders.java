	package com.project.it.expert;

    import android.app.Activity;
    import android.content.Context;
    import android.content.Intent;
    import android.database.Cursor;
    import android.database.SQLException;
    import android.database.sqlite.SQLiteDatabase;
    import android.os.Bundle;
    import android.view.KeyEvent;
    import android.widget.ListView;

    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.HashMap;

    import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

    public class List_Orders extends Activity {
        private String hamyarcode;
        private String guid;
        private ListView lstOrder;
        private DatabaseHelper dbh;
        private SQLiteDatabase db;
        //private Button btnCredit;
        //private Button btnOrders;
        //private Button btnHome;
        private ArrayList<HashMap<String ,String>> valuse=new ArrayList<HashMap<String, String>>();
        @Override
        protected void attachBaseContext(Context newBase) {
            super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        }
        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_menu_list_order);
//            btnCredit=(Button)findViewById(R.id.btnCredit);
//            btnOrders=(Button)findViewById(R.id.btnOrders);
//            btnHome=(Button)findViewById(R.id.btnHome);
            lstOrder=(ListView)findViewById(R.id.lstOrder);
            dbh=new DatabaseHelper(getApplicationContext());
            try {

                dbh.createDataBase();

            } catch (IOException ioe) {

                throw new Error("Unable to create database");

            }

            try {

                dbh.openDataBase();

            } catch (SQLException sqle) {

                throw sqle;
            }
        try
        {
            hamyarcode = getIntent().getStringExtra("hamyarcode").toString();
            guid = getIntent().getStringExtra("guid").toString();
        }
        catch (Exception e)
        {
            db=dbh.getReadableDatabase();
            Cursor coursors = db.rawQuery("SELECT * FROM login",null);
            for(int i=0;i<coursors.getCount();i++){
                coursors.moveToNext();
                guid=coursors.getString(coursors.getColumnIndex("guid"));
                hamyarcode=coursors.getString(coursors.getColumnIndex("hamyarcode"));
            }
            db.close();
        }
            db=dbh.getReadableDatabase();
            Cursor coursors = db.rawQuery("SELECT BsUserServices.*,Servicesdetails.name FROM BsUserServices " +
                    "LEFT JOIN " +
                    "Servicesdetails ON " +
                    "Servicesdetails.code=BsUserServices.ServiceDetaileCode",null);
            for(int i=0;i<coursors.getCount();i++){
                coursors.moveToNext();
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Code",coursors.getString(coursors.getColumnIndex("Code")));
                map.put("TitleOrder",coursors.getString(coursors.getColumnIndex("name")));
                map.put("OrderDate",coursors.getString(coursors.getColumnIndex("StartDate")));
                map.put("Addres",coursors.getString(coursors.getColumnIndex("AddressText")));
                valuse.add(map);
            }
            db.close();
            AdapterServices dataAdapter=new AdapterServices(this,valuse,guid,hamyarcode);
            lstOrder.setAdapter(dataAdapter);
            

//            btnCredit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    LoadActivity(Credit.class, "guid",  guid, "hamyarcode", hamyarcode);
//                }
//            });
//            btnOrders.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    LoadActivity(History.class, "guid", guid, "hamyarcode", hamyarcode);
//                }
//            });
//            btnHome.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    LoadActivity(MainMenu.class, "guid", guid, "hamyarcode", hamyarcode);
//                }
//            });
    }
    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event )  {
        if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
            List_Orders.this.LoadActivity(MainMenu.class, "guid", guid, "hamyarcode", hamyarcode);
        }

        return super.onKeyDown( keyCode, event );
    }
    public void LoadActivity(Class<?> Cls, String VariableName, String VariableValue, String VariableName2, String VariableValue2)
        {
            Intent intent = new Intent(getApplicationContext(),Cls);
            intent.putExtra(VariableName, VariableValue);
            intent.putExtra(VariableName2, VariableValue2);
            List_Orders.this.startActivity(intent);
        }
    }
