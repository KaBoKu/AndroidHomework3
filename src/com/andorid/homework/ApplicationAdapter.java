package com.andorid.homework;

import java.text.Collator;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.android.homework.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

public class ApplicationAdapter extends ArrayAdapter<ApplicationInfo> {
	private List<ApplicationInfo> appsList = null;
	private Context context;
	private PackageManager packageManager;
	private float rating;
	private SharedPreferences preferences;
	private SharedPreferences.Editor preferencesEditor;
	public static final String MY_PREFERENCES = "myPreferences";

	static class ViewHolder {
		public TextView appName;
		public RatingBar ratingBar;
		public ImageView iconview;
	}

	public ApplicationAdapter(Context context, int textViewResourceId,
			List<ApplicationInfo> appsList) {
		super(context, textViewResourceId, appsList);
		this.context = context;
		this.appsList = appsList;
		packageManager = context.getPackageManager();

		preferences = context.getSharedPreferences(MY_PREFERENCES,
				context.MODE_PRIVATE);
	}

	@Override
	public int getCount() {
		return ((null != appsList) ? appsList.size() : 0);
	}

	@Override
	public ApplicationInfo getItem(int position) {
		return ((null != appsList) ? appsList.get(position) : null);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (null == view) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.snippet_list_row, null);
			ViewHolder vHolder = new ViewHolder();
			vHolder.appName = (TextView) view.findViewById(R.id.app_name);
			vHolder.iconview = (ImageView) view.findViewById(R.id.app_icon);
			vHolder.ratingBar = (RatingBar) view.findViewById(R.id.ratingbar);
			view.setTag(vHolder);
		}

		final ApplicationInfo data = appsList.get(position);
		if (null != data) {/*
							 * preferences =
							 * context.getSharedPreferences(MY_PREFERENCES,
							 * context.MODE_PRIVATE);
							 */
			ViewHolder vHolder = (ViewHolder)view.getTag();
			/*TextView appName = (TextView) view.findViewById(R.id.app_name);
			RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingbar);

			ImageView iconview = (ImageView) view.findViewById(R.id.app_icon);
			vHolder.appName.setText(R.id.app_name);
			vHolder.ratingBar.setRating(R.id.ratingbar);
			vHolder.iconview.setImageURI(R.id.app_icon);*/
			vHolder.iconview.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = packageManager
							.getLaunchIntentForPackage(data.packageName);
					context.startActivity(intent);
				}
			});

			vHolder.ratingBar
					.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

						@Override
						public void onRatingChanged(RatingBar ratingBar,
								float rating, boolean fromUser) {
							// TODO Auto-generated method stub

							preferencesEditor = preferences.edit();
							preferencesEditor.putFloat(
									data.loadLabel(packageManager).toString(),
									ratingBar.getRating());
							System.out.println("lllllllllllllllllllllllllllL: "
									+ data.loadLabel(packageManager).toString()
									+ " " + data.name + " " + " "
									+ data.packageName);
							preferencesEditor.commit();

						}
					});

			vHolder.appName.setText(data.loadLabel(packageManager));
			vHolder.ratingBar.setRating(preferences.getFloat(
					data.loadLabel(packageManager).toString(), 1.0f));
			vHolder.iconview.setImageDrawable(data.loadIcon(packageManager));
		}
		return view;
	}

	public ApplicationAdapter sortLex() {
		Collections.sort(appsList, new ApplicationInfo.DisplayNameComparator(
				packageManager));
		this.notifyDataSetChanged();
		return this;
	};

	public ApplicationAdapter sortLexDesc() {
		Collections.sort(appsList, new DisplayNameComparator2(packageManager));
		this.notifyDataSetChanged();
		return this;
	}

	public ApplicationAdapter sortRating() {
		Collections.sort(appsList, new RatingComparator(packageManager));
		this.notifyDataSetChanged();
		return this;
	}

	public ApplicationAdapter sortRaingDesc() {
		Collections.sort(appsList, new RatingComparatorDesc(packageManager));
		this.notifyDataSetChanged();
		return this;
	}

	public static class DisplayNameComparator2 implements
			Comparator<ApplicationInfo> {
		public DisplayNameComparator2(PackageManager pm) {
			mPM = pm;
		}

		public final int compare(ApplicationInfo aa, ApplicationInfo ab) {
			CharSequence sa = mPM.getApplicationLabel(aa);
			if (sa == null) {
				sa = aa.packageName;
			}
			CharSequence sb = mPM.getApplicationLabel(ab);
			if (sb == null) {
				sb = ab.packageName;
			}

			return sCollator.compare(sb.toString(), sa.toString());
		}

		private final Collator sCollator = Collator.getInstance();
		private PackageManager mPM;
	}

	public class RatingComparator implements Comparator<ApplicationInfo> {
		PackageManager pm;

		public RatingComparator(PackageManager pm) {
			super();
			// TODO Auto-generated constructor stub
			this.pm = pm;
		}

		public final int compare(ApplicationInfo aa, ApplicationInfo ab) {
			CharSequence sa = pm.getApplicationLabel(aa);
			System.out.println(sa.toString());
			if (sa == null) {
				sa = aa.packageName;
			}
			CharSequence sb = pm.getApplicationLabel(ab);

			if (sb == null) {
				sb = ab.packageName;
			}

			System.out.println(sb.toString());
			Float saRating = preferences.getFloat(sa.toString(), 0);
			Float sbRating = preferences.getFloat(sb.toString(), 0);
			System.out.println("======================================");
			System.out.println(saRating + " " + sbRating);
			System.out.println(sa.toString() + " " + sb.toString());
			System.out.println(aa.name + " " + ab.name);
			System.out.println(aa.packageName + " " + ab.packageName);

			System.out.println(Float.compare(saRating, sbRating));
			return Float.compare(saRating, sbRating);
		}

	}

	public class RatingComparatorDesc implements Comparator<ApplicationInfo> {
		PackageManager pm;

		public RatingComparatorDesc(PackageManager pm) {
			super();
			// TODO Auto-generated constructor stub
			this.pm = pm;
		}

		public final int compare(ApplicationInfo aa, ApplicationInfo ab) {
			CharSequence sa = pm.getApplicationLabel(aa);
			System.out.println(sa.toString());
			if (sa == null) {
				sa = aa.packageName;
			}
			CharSequence sb = pm.getApplicationLabel(ab);

			if (sb == null) {
				sb = ab.packageName;
			}

			System.out.println(sb.toString());
			Float saRating = preferences.getFloat(sa.toString(), 0);
			Float sbRating = preferences.getFloat(sb.toString(), 0);
			System.out.println("======================================");
			System.out.println(saRating + " " + sbRating);
			System.out.println(sa.toString() + " " + sb.toString());
			System.out.println(aa.name + " " + ab.name);
			System.out.println(aa.packageName + " " + ab.packageName);

			System.out.println(Float.compare(sbRating, saRating));
			return Float.compare(sbRating, saRating);
		}

	}

}