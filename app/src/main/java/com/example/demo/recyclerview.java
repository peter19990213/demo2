package com.example.demo;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;



public class recyclerview extends AppCompatActivity {
    private EditText longitude ;
    private EditText latitude ;
    private EditText name ;
    private Button btn ;
    private int _id_temp;
    private MyAdapter myAdapter;
    private RecyclerView rv;
    private ArrayList myDataset;
    private int r_list[] ;
    private Float r_list_long[] ;
    private Float r_list_lat[] ;



    private final static String CREATE_TABLE=
            "create table location" +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "_longitude REAL NOT NULL," +
                    "_latitude REAL NOT NULL," +
                    "_name TEXT" +
                    ");";

    protected void onCreate(Bundle savedInstanceState ){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        longitude=(EditText)findViewById(R.id.longitude) ;
        latitude=(EditText)findViewById(R.id.latitude) ;
        name=(EditText)findViewById(R.id.name) ;
        rv=(RecyclerView) findViewById(R.id.rv);
        btn=(Button)findViewById(R.id.btn_add);
        SQLiteDatabase db=null;
        db=openOrCreateDatabase("db1.db",MODE_PRIVATE,null);
        try{
            db.execSQL(CREATE_TABLE);
        }catch (Exception e){}

        seach_sqlite_toRecyclerView();
        rv_onswiped_move();
        rv_onclick_onlongclick();

        btn.setOnClickListener(btn_on_click);


        // btn.setOnClickListener(btndo);





    }
    protected void onDestroy() {
        super.onDestroy();
        SQLiteDatabase db=null;
        db=openOrCreateDatabase("db1.db",MODE_PRIVATE,null);
        db.execSQL("DROP TABLE location");
        db.close();

    }


    private Button.OnClickListener btn_on_click = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            add_sqlite();
        }
    };


    private void seach_sqlite_toRecyclerView(){
        SQLiteDatabase db=null;
        db=openOrCreateDatabase("db1.db",MODE_PRIVATE,null);

            myDataset= new ArrayList<Item>();

            Cursor c= db.rawQuery("SELECT * FROM location ",null);
            r_list=new int[c.getCount()];
            r_list_long=new Float[c.getCount()];
            r_list_lat=new Float[c.getCount()];
            if (c.getCount()>=0){
                Item item ;
                for(int i =0 ; i < c.getCount() ; i++){

                    c.moveToNext();
                    item = new Item();
                    item.set_id(c.getInt(0)+"");
                    item.set_longitude(c.getFloat(1)+"");
                    item.set_latitude(c.getFloat(2)+"");
                    item.set_name(c.getString(3));
                    r_list[i]=c.getInt(0);
                    r_list_long[i]=c.getFloat(1);
                    r_list_lat[i]=c.getFloat(2);
                    _id_temp=c.getInt(0);

                    myDataset.add(item);

                }

                myAdapter=new MyAdapter(myDataset);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                rv.setLayoutManager(layoutManager);
                rv.setAdapter(myAdapter);



        }
        db.close();


    }
    private void add_sqlite(){
        SQLiteDatabase db=null;
        db=openOrCreateDatabase("db1.db",MODE_PRIVATE,null);
        try{
            ContentValues cv=new ContentValues();
            Double _longitude = Double.parseDouble(longitude.getText().toString());
            Double _latitude = Double.parseDouble(latitude.getText().toString());
            String _name=name.getText().toString();
            cv.put("_longitude",_longitude);
            cv.put("_latitude",_latitude);
            cv.put("_name",_name);
            db.insert("location",null,cv);
        }
        finally {
            
            db.close();
            seach_sqlite_toRecyclerView();
            Toast t= Toast.makeText(recyclerview.this,"content://com.example.demo/locations/"+
                    (_id_temp),Toast.LENGTH_LONG);
            t.show();

            longitude.setText("");
            latitude.setText("");
            name.setText("");

        }



    }

    private static class Item{
        String _id;
        String _longitude;
        String _latitude;
        String _name;

        public String get_id() {
            return _id;
        }

        public void set_id(String text) {
            this._id = text;
        }

        public String get_longitude() {
            return _longitude;
        }

        public void set_longitude(String text) {
            this._longitude = text;
        }

        public String get_latitude() {
            return _latitude;
        }

        public void set_latitude(String text) {
            this._latitude = text;
        }

        public String get_name() {
            return _name;
        }

        public void set_name(String text) {
            this._name = text;
        }



    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Item> mData;

        public class ViewHolder extends RecyclerView.ViewHolder  {
            public TextView _id;
            public TextView _longitude;
            public TextView _latitude;
            public TextView _name;

            public ViewHolder(View v) {
                super(v);
                _id = (TextView) v.findViewById(R.id._id);
                _longitude = (TextView) v.findViewById(R.id._longitude);
                _latitude = (TextView) v.findViewById(R.id._latitude);
                _name = (TextView) v.findViewById(R.id._name);


            }

        }

        public MyAdapter(List<Item> data) {
            mData = data;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            Item item = mData.get(position);
            holder._id.setText(item.get_id());
            holder._longitude.setText(item.get_longitude());
            holder._latitude.setText(item.get_latitude());
            holder._name.setText(item.get_name());

        }
        public int getItemCount(){return mData.size();}
    }

    private void rv_onswiped_move(){
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }


            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                final int w=viewHolder.getLayoutPosition();

                SQLiteDatabase db=null;
                db=openOrCreateDatabase("db1.db",MODE_PRIVATE,null);
           try{
               String __id =String.valueOf(r_list[w]);
               String sql="_id = "+__id;
               db.delete("location",sql,null);
           }finally {
               Toast t = Toast.makeText(recyclerview.this,r_list[w]+"",Toast.LENGTH_LONG);
               t.show();
               db.close();
               seach_sqlite_toRecyclerView();
           }





            }
        }).attachToRecyclerView(rv);


    }
    private void rv_onclick_onlongclick(){
        rv.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), rv, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                float _long=r_list_long[position];
                float _lat=r_list_lat[position];
                seach_close_point(position,_long,_lat);

            }

            @Override
            public void onItemLongClick(View view, int position) {
                float _long=r_list_long[position];
                float _lat=r_list_lat[position];
                openmap(_long,_lat);
            }
        }));
    }
    private void seach_close_point(int position,float _long,float _lat)
    {

        if(r_list.length>1){
            double distance_min  = Integer.MAX_VALUE;
            int point_min=-1;
            for (int i =0;i<r_list.length;i++){
                if (i!=position)
                {
                    double distance =  Math.pow(_long-r_list_long[i],2) + Math.pow(_lat-r_list_lat[i],2);
                    if (distance<distance_min)
                    {
                        point_min=r_list[i];
                        distance_min=distance;

                    }


                }



            }
            Toast.makeText(recyclerview.this,""+point_min,Toast.LENGTH_LONG).show();

        }
        else
            Toast.makeText(recyclerview.this,"null",Toast.LENGTH_LONG).show();







    }
    private void openmap(float _long,float _lat){
        Uri w = Uri.parse("geo:0.0?q="+_lat+", "+_long+"(Google+Sydney)");
        Intent map = new Intent(Intent.ACTION_VIEW,w);
        if(map.resolveActivity(getPackageManager())!=null){
            startActivity(map);
        }
    }

}
