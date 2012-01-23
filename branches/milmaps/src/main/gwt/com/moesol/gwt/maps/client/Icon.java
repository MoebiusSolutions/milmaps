package com.moesol.gwt.maps.client;

import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Provider;

/**
 * Icon and label. When enabled the label for the icon will be decluttered on the map.
 * 
 * @author <a href="http://www.moesol.com">Moebius Solutions, Inc.</a>
 * @author hastings
 */
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
	private int m_zIndex = 4010;
	private Image m_image = IMAGE_PROVIDER.get();
	private ViewDimension m_imageSize = new ViewDimension(32, 32);
	private Image m_labelLeaderImage = null;
	private String m_labelLeaderImageUrl = null;
	private ViewCoords m_offsetWithInLabelLeaderImage = new ViewCoords();
	
	private HandlerRegistration loadRegistration;
	private HandlerRegistration errorRegistration;
	private final LoadHandler m_loadHandler = new LoadHandler() {
		@Override
		public void onLoad(LoadEvent event) {
			loadRegistration.removeHandler();
		}
	};
	private final ErrorHandler m_errorHandler = new ErrorHandler() {
		@Override
		public void onError(ErrorEvent event) {
			Image image = (Image) event.getSource();
			errorRegistration.removeHandler();
			image.setUrl(MISSING_IMAGE_URL);
		}
	};

    @Override
	public String toString() {
		return "Icon [m_location=" + m_location + ", m_iconOffset="
				+ m_iconOffset + ", m_declutterOffset=" + m_declutterOffset
				+ ", m_iconUrl=" + m_iconUrl + ", m_clickUrl=" + m_clickUrl
				+ ", m_label=" + m_label + ", m_zIndex=" + m_zIndex
				+ ", m_image=" + m_image + ", m_imageSize=" + m_imageSize
				+ ", m_labelLeaderImage=" + m_labelLeaderImage
				+ ", m_labelLeaderImageUrl=" + m_labelLeaderImageUrl
				+ ", m_offsetWithInLabelLeaderImage="
				+ m_offsetWithInLabelLeaderImage + ", loadRegistration="
				+ loadRegistration + ", errorRegistration=" + errorRegistration
				+ ", m_loadHandler=" + m_loadHandler + ", m_errorHandler="
				+ m_errorHandler + "]";
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
		loadRegistration = m_image.addLoadHandler(m_loadHandler);
		errorRegistration = m_image.addErrorHandler(m_errorHandler);
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
	public void setImage(Image image) {
		m_image = image;
	}
	public ViewDimension getImageSize() {
		return m_imageSize;
	}
	/**
	 * Image size in pixels. Used during declutter if no image is set.
	 * Also used to force the layout of the image to this size.
	 * @param size
	 */
	public void setImageSize(ViewDimension size) {
		m_imageSize = size;
	}
	
	public Label getLabel() {
		return m_label;
	}
	
	public Image getLabelLeaderImage() {
		return m_labelLeaderImage;
	}

	public void setLabelLeaderImage(Image labelLeaderImage) {
		m_labelLeaderImage = labelLeaderImage;
	}
	
	public String getLabelLeaderImageUrl() {
		return m_labelLeaderImageUrl;
	}

	public void setLabelLeaderImageUrl(String url) {
		m_labelLeaderImageUrl = url;
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

	public int getZIndex() {
		return m_zIndex;
	}
	public void setZIndex(int zIndex) {
		m_zIndex = zIndex;
	}

	public ViewCoords getOffsetWithInLabelLeaderImage() {
		return m_offsetWithInLabelLeaderImage;
	}

	public void setOffsetWithinLabelLeaderImage(ViewCoords offsetWithInLabelLeaderImage) {
		m_offsetWithInLabelLeaderImage = offsetWithInLabelLeaderImage;
	}
	
}
