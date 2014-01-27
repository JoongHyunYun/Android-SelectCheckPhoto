package com.project.make.album;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.project.make.toaststyle.CommonToast;
import com.project.make.upload.UploadActivity;

import com.project.make.R;

public class MultiPhotoSelectActivity extends BaseActivity {

	private ArrayList<String> imageUrls;
	private DisplayImageOptions options;
	private ImageAdapter imageAdapter;
	String PanType,Title,Name;
	int cnt=0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_grid);

		//Bundle bundle = getIntent().getExtras();
		//imageUrls = bundle.getStringArray(Extra.IMAGES);
		
		PanType=getIntent().getStringExtra("PanType");
		Title=getIntent().getStringExtra("Title");
		Name=getIntent().getStringExtra("Name");

		final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
		final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
		Cursor imagecursor = getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
				null, orderBy + " DESC");
		
		this.imageUrls = new ArrayList<String>();
		
		for (int i = 0; i < imagecursor.getCount(); i++) {
			imagecursor.moveToPosition(i);
			int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
			imageUrls.add(imagecursor.getString(dataColumnIndex));
		}
		
		options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.stub_image)
			//.showStubImage(R.drawable.ic_launcher1)
			.showImageForEmptyUri(R.drawable.image_for_empty_url)
			.cacheInMemory()
			.cacheOnDisc()
			.build();

		imageAdapter = new ImageAdapter(this, imageUrls);
		
		GridView gridView = (GridView) findViewById(R.id.gridview);
		gridView.setAdapter(imageAdapter);
		
		/*gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id ) {
				// TODO Auto-generated method stub
				//Toast.makeText(getApplicationContext(), "srm"+(position+1)+"selected", Toast.LENGTH_LONG).show();
			}
		});*/
	}

	@Override
	protected void onStop() {
		imageLoader.stop();
		super.onStop();
	}

	public void btnChoosePhotosClick(View v){
		
		//ArrayList<String> selectedItems = imageAdapter.getCheckedItems();
		//Toast.makeText(MultiPhotoSelectActivity.this, "Total photos selected:"+selectedItems.size(), Toast.LENGTH_SHORT).show();
		//Toast.makeText(MultiPhotoSelectActivity.this, "Total photos selected: "+selectedItems.toString(), Toast.LENGTH_LONG).show();
		//Log.d(MultiPhotoSelectActivity.class.getSimpleName(), "Selected Items: " + selectedItems.toString());
		
		String items1="";
		
		int len = imageAdapter.getCheckedItems().size();
		
		if(len==15){
		
			for (int i = 0; i < len; i++) {
				items1 = items1
							+ imageAdapter.getCheckedItems().get(i).toString()+ ",";
			
			}
				items1 = items1.substring(0,items1.lastIndexOf(","));
				
				Intent intent = new Intent(MultiPhotoSelectActivity.this,
						UploadActivity.class);
				intent.putExtra("fileName", items1);
				intent.putExtra("PanType", PanType); //split으로 짤라 써야한다.
				intent.putExtra("Title", Title);
				intent.putExtra("Name", Name);
				startActivity(intent);
		}else if(len<15){
			CommonToast t = new CommonToast(getApplicationContext());
			    t.showToast(""+(15-len)+"장 더 선택해주세요.", Toast.LENGTH_SHORT);
		}else{
			CommonToast t = new CommonToast(getApplicationContext());
		    t.showToast(""+(len-15)+"장 초과되었습다.", Toast.LENGTH_SHORT);
		}
	}
	public void btnChoosePhotoEnd(View v){
		moveTaskToBack(true);
		finish();
	}
	
	/*private void startImageGalleryActivity(int position) {
		Intent intent = new Intent(this, ImagePagerActivity.class);
		intent.putExtra(Extra.IMAGES, imageUrls);
		intent.putExtra(Extra.IMAGE_POSITION, position);
		startActivity(intent);
	}*/

	public class ImageAdapter extends BaseAdapter {
		
		ArrayList<String> mList;
		LayoutInflater mInflater;
		Context mContext;
		SparseBooleanArray mSparseBooleanArray;
		
		public ImageAdapter(Context context, ArrayList<String> imageList) {
			// TODO Auto-generated constructor stub
			mContext = context;
			mInflater = LayoutInflater.from(mContext);
			mSparseBooleanArray = new SparseBooleanArray();
			mList = new ArrayList<String>();
			this.mList = imageList;

		}
		
		public ArrayList<String> getCheckedItems() {
			ArrayList<String> mTempArry = new ArrayList<String>();

			for(int i=0;i<mList.size();i++) {
				if(mSparseBooleanArray.get(i)) {
					mTempArry.add(mList.get(i));
				}
			}

			return mTempArry;
		}
		
		@Override
		public int getCount() {
			return imageUrls.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.row_multiphoto_item, null);
			}

			CheckBox mCheckBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
			final ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView1);
			
			imageLoader.displayImage("file://"+imageUrls.get(position), imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingComplete(Bitmap loadedImage) {
					Animation anim = AnimationUtils.loadAnimation(MultiPhotoSelectActivity.this, R.anim.fade_in);
					imageView.setAnimation(anim);
					anim.start();
				}
			});
			
			mCheckBox.setTag(position);
			mCheckBox.setChecked(mSparseBooleanArray.get(position));
			mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
			
			
			return convertView;
		}
		
		OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				
				
				TextView tv = (TextView)findViewById(R.id.countview);
				
				if(isChecked){
					buttonView.setSelected(true);
					mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
				//	Toast.makeText(getApplicationContext(), "true:::"+i, Toast.LENGTH_SHORT).show();
				}else{
					buttonView.setSelected(false);
					mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
				//	Toast.makeText(getApplicationContext(), "false:::"+i, Toast.LENGTH_SHORT).show();
				}
				
				int i = CountNumber();
				String text = i+"/15";
				tv.setText(text);
			//	Toast.makeText(getApplicationContext(), "false:::"+i, Toast.LENGTH_SHORT).show();
				
			}
		};
	}
		
	
	public int CountNumber(){
		
		return imageAdapter.getCheckedItems().size();
	}
	
}
