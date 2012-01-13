package com.moesol.gwt.maps.client;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LoadListener;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Provider;

public class Icon {
	static Provider<Image> IMAGE_PROVIDER = new Provider<Image>() {
		@Override
		public Image get() {
			return new Image();
		}
	};
	static Provider<Label> LABEL_PROVIDER = new Provider<Label>() {
		@Override
		public Label get() {
			return new Label();
		}
	};
	static LabelStyler LABEL_STYLER = new LabelStyler() {
		@Override
		public void setWordWrap(Label label, boolean v) {
			label.setWordWrap(v);
		}
		@Override
		public void setColor(Label label, String color) {
			label.getElement().getStyle().setColor(color);
		}
		@Override
		public void setZIndex(Label label, int index) {
			label.getElement().getStyle().setZIndex(index);
		}
	};
	interface LabelStyler {
		void setWordWrap(Label label, boolean v);
		void setColor(Label label, String color);
		void setZIndex(Label label, int index);
	}
	private GeodeticCoords m_location = new GeodeticCoords();
	private ViewCoords m_iconOffset = new ViewCoords();
	private ViewCoords m_declutterOffset = new ViewCoords(4, 0);
	private static String MISSING_IMAGE_URL = "missing.gif";
	private String m_iconUrl;
	private String m_clickUrl;
	private Label m_label = null;
	private Image m_image = IMAGE_PROVIDER.get();
	private int m_zIndex = 2010;
	private final LoadListener m_loadListener = new LoadListener() {
		@Override
		public void onError(Widget w) {
			Image image = (Image) w;
			image.removeLoadListener(this);
			image.setUrl(MISSING_IMAGE_URL);
		}
		@Override
		public void onLoad(Widget w) {
			Image image = (Image) w;
			image.removeLoadListener(this);
		}
	};

    @Override
    public String toString()
    {
        return "Icon{" + "m_location=" + m_location + ", m_iconOffset=" + m_iconOffset + ", m_label=" + m_label + ", m_zIndex=" + m_zIndex + '}';
    }

    public Icon() {
	}
	
	public Icon(int zIndex) {
		m_zIndex = zIndex;
		m_image.getElement().getStyle().setZIndex( m_zIndex );
	}
	
	public String getTitle() {
		return m_image.getTitle();
	}
	
	public void setTitle(String title) {
		m_image.setTitle(title);
	}
	
	public String getLabelText(){
		if ( m_label != null )
			return m_label.getText();
		return null;
	}
	
	public void setLabel(String text){
		if (m_label == null) {
			m_label = LABEL_PROVIDER.get();
			LABEL_STYLER.setWordWrap(m_label, false);
			LABEL_STYLER.setColor(m_label, "yellow");
			LABEL_STYLER.setZIndex(m_label, m_zIndex);
		}
		m_label.setText(text);
	}

	public String getIconUrl() {
		return m_iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		m_iconUrl = iconUrl;
		m_image.addLoadListener(m_loadListener);
		m_image.setUrl(iconUrl);
	}

	public String getClickUrl() {
		return m_clickUrl;
	}

	public void setClickUrl(String clickUrl) {
		m_clickUrl = clickUrl;
	}
	
	public GeodeticCoords getLocation() {
		return m_location;
	}

	public void setLocation(GeodeticCoords location) {
		m_location = location;
	}
	
	public Image getImage() {
		return m_image;
	}
	
	public Label getLabel(){
		return m_label;
	}

	public ViewCoords getIconOffset() {
		return m_iconOffset;
	}
	/**
	 * The default icon offset is 0, 0, the upper left hand corner of
	 * the icon. The offset is added to the icon position when placed on the map.
	 * To center an icon set the x offset to -width/2 and the y offset to -height/2.
	 * 
	 * @param offset
	 */
	public void setIconOffset(ViewCoords offset) {
		m_iconOffset = offset;
	}
	
	public ViewCoords getDeclutterOffset() {
		return m_declutterOffset;
	}
	public void setDeclutterOffset(ViewCoords offset) {
		m_declutterOffset = offset;
	}

}
