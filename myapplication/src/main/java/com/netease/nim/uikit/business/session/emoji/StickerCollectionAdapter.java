package com.netease.nim.uikit.business.session.emoji;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.netease.nim.uikit.R;

/**
 * 每屏显示的贴图  收藏
 */
public class StickerCollectionAdapter extends BaseAdapter {

    private Context context;
    private StickerCategory category;
    private int startIndex;
    public StickerCollectionAdapter(Context mContext, StickerCategory category, int startIndex) {
        this.context = mContext;
        this.category = category;
        this.startIndex = startIndex;
    }

    public StickerCategory getCategory() {
        return category;
    }

    public void setCategory(StickerCategory category) {
        this.category = category;
        notifyDataSetChanged();
    }

    public int getCount() {//获取每一页的数量
        int count = category.getStickers().size() - startIndex;
        count = Math.min(count, EmoticonView.STICKER_PER_PAGE);
       // StickerItem
        return count;
    }

    @Override
    public Object getItem(int position) {
        return category.getStickers().get(startIndex + position);
    }

    @Override
    public long getItemId(int position) {
        return startIndex + position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        StickerViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.nim_sticker_picker_view, null);
            viewHolder = new StickerViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.sticker_thumb_image);
            viewHolder.descLabel = (TextView) convertView.findViewById(R.id.sticker_desc_label);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (StickerViewHolder) convertView.getTag();
        }

        int index = startIndex + position;
        if (index >= category.getStickers().size()) {
            return convertView;
        }
        System.out.println("----------------1-startIndex="+startIndex);
        System.out.println("----------------2-category.getStickers()="+ category.getStickers().size());
        StickerItem sticker = category.getStickers().get(index);
        System.out.println("----------------2-sticker.getName()="+sticker.getName());
        if (sticker == null) {
            return convertView;
        }
        System.out.println("----------------2-position="+position);
        if(sticker.getName().equals("1")){
            viewHolder.imageView.setImageResource(R.drawable.collect0001);
        }else{
            String imgurl=sticker.getName();
            Glide.with(context)
                    .load(imgurl)
                    .apply(new RequestOptions()
                            .error(R.drawable.nim_default_img_failed)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .dontAnimate())
                    .into(viewHolder.imageView);
        }



        viewHolder.descLabel.setVisibility(View.GONE);

        return convertView;
    }

    class StickerViewHolder {
        public ImageView imageView;
        public TextView descLabel;
    }
}