package com.moesol.gwt.maps.client.controls;

import com.google.gwt.core.client.GWT;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.Icon;
import com.moesol.gwt.maps.client.MapView;
import com.moesol.gwt.maps.client.ViewCoords;
import java.io.Serializable;

public class TagControl extends Composite implements Serializable {

    private boolean m_bTagOn = false;
    private String m_name;
    GeodeticCoords m_gc;
    MapView m_mapView;
    private AddTagDialog m_box = null;
    private int m_nameCount = 0;
    private TagControlServiceAsync tagControlService = GWT.create(TagControlService.class);

    // add tag dialog class ----------------------------------
    private class AddTagDialog extends DialogBox {

        public AddTagDialog() {
            super();
        }

        public boolean setTag(MapView map, GeodeticCoords gc, String name) {
            if (name != null && name.length() > 1) {
                final List<Icon> icons = m_mapView.getIconLayer().getIcons();
                boolean bUnique = true;
                for (Icon icon : icons) {
                    String val = icon.getLabelText();
                    if (val != null && name.compareTo(val) == 0) {
                        bUnique = false;
                        break;
                    }
                }
                if (bUnique == true) {
                    hide();
                    Icon icon = new Icon(2010);
                    icon.setLocation(gc);
                    //String url = "http://www.moesol.com/products/mx/js/mil_picker/mil_picker_images/sfapmfq--------.jpeg";
                    String url = "images/icon.jpeg";
                    icon.setIconUrl(url);
                    icon.setLabel(name);
                    Image im = icon.getImage();
                    im.setPixelSize(16, 16);
                    map.getIconLayer().addIcon(icon);
                    map.updateView();
                    return true;
                } else {//choose a unique name
                    Window.alert("Choose a unique label");
                }
            } else {
                Window.alert("You must enter a label");
            }
            return false;
        }
    }
    // End of add tag dialog class ------------------------------------

    // remove tag dialog class ----------------------------------
    private class RemoveTagDialog extends DialogBox {

        public RemoveTagDialog() {
            super();
        }
    }
    // End of add tag dialog class ------------------------------------

    public TagControl() {
    }

    public TagControl(MapView mapView, boolean bHorizontal) {
        super();
        setMapView(mapView, bHorizontal);
    }

    public void setMapView(MapView mapView, boolean bHorizontal) {

        m_mapView = mapView;
        MouseDownHandler mousedownHandler = new MouseDownHandler() {

            @Override
            public void onMouseDown(MouseDownEvent event) {
                mouseDown(event.getX(), event.getY());
            }
        };

        m_mapView.addDomHandler(mousedownHandler, MouseDownEvent.getType());


        MouseUpHandler mouseUphandler = new MouseUpHandler() {

            @Override
            public void onMouseUp(MouseUpEvent event) {
                mouseUp(event.getX(), event.getY());
            }
        };

        m_mapView.addDomHandler(mouseUphandler, MouseUpEvent.getType());
        MapButton deleteBtn = new MapButton();
        deleteBtn.addStyleName("map-TagControlPositionOffButton");

        MapButton addBtn = new MapButton();
        addBtn.addStyleName("map-TagControlPositionOnButton");

        deleteBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                RemoveTagDialog dlg = removeDialogWidget("Delete tags");
                int x = m_mapView.getViewport().getWidth() / 2;
                int y = m_mapView.getViewport().getHeight() / 2;
                dlg.setPopupPosition(x, y);
                dlg.show();

            }
        });

        addBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                m_mapView.setFocus(false);
                m_mapView.getController().setUseDragTracker(false);
                m_bTagOn = true;
            }
        });

        if (bHorizontal) {
            HorizontalPanel p = new HorizontalPanel();
            p.add(deleteBtn);
            p.add(addBtn);
            initWidget(p);
        } else {
            VerticalPanel p = new VerticalPanel();
            p.add(deleteBtn);
            p.add(addBtn);
            initWidget(p);
        }
        addStyleName("map-TagControl");
        setZindex(100000);

        // Initialize the service proxy.
        if (tagControlService == null) {
            tagControlService = GWT.create(TagControlService.class);
        }
        // Set up the callback object.
        AsyncCallback<Tag[]> callback = new AsyncCallback<Tag[]>() {

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Internal Server Error:" + caught.getMessage());
            }

            @Override
            public void onSuccess(Tag[] tags) {
                for (Tag tag : tags) {
                    Icon icon = new Icon(2010);
                    icon.setLocation(tag.getGeodeticCoords());
                    //String url = "http://www.moesol.com/products/mx/js/mil_picker/mil_picker_images/sfapmfq--------.jpeg";
                    String url = "images/icon.jpeg";
                    icon.setIconUrl(url);
                    icon.setLabel(tag.getName());
                    Image im = icon.getImage();
                    im.setPixelSize(16, 16);
                    m_mapView.getIconLayer().addIcon(icon);
                    m_mapView.updateView();
                }
            }
        };
        tagControlService.loadTagsFromDisk(callback);
    }

    public void mouseDown(int x, int y) {
        if (m_bTagOn) {
        	ViewCoords vc = new ViewCoords(x, y);
            m_mapView.setFocus(false);
            m_gc = m_mapView.getProjection().viewToGeodetic(vc);
            m_bTagOn = false;
            if (m_box == null) {
                m_box = addDialogWidget("Add Tag");
            }
            m_box.setPopupPosition(x, y);
            m_box.show();
        }
    }

    public void mouseUp(int x, int y) {
        m_bTagOn = false;
        m_mapView.getController().setUseDragTracker(true);
        m_mapView.setFocus(true);
    }

    public void setZindex(int zIndex) {
        this.getElement().getStyle().setZIndex(zIndex);
    }

    public AddTagDialog addDialogWidget(final String header) {//, String content) {
        final AddTagDialog box = new AddTagDialog();
        box.setText(header);

        final VerticalPanel panel = new VerticalPanel();
        final TextArea ta = new TextArea();
        ta.setCharacterWidth(20);
        ta.setVisibleLines(1);
        ta.setReadOnly(false);
        panel.add(ta);
        final Button btnSave = new Button("Save");
        final Button btnCancel = new Button("Cancel");
        ClickHandler saveHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                m_name = ta.getText();
                if (box.setTag(m_mapView, m_gc, m_name) == true) {
                    m_bTagOn = false;
                    saveTagToDisk();
                }
            }

            private void saveTagToDisk() {
                // Initialize the service proxy.
                if (tagControlService == null) {
                    tagControlService = GWT.create(TagControlService.class);
                }
                // Set up the callback object.
                AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Internal Server Error:" + caught.getMessage());
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                    }
                };
                tagControlService.saveTagToDisk(m_name, m_gc, callback);
            }
        };
        btnSave.addClickHandler(saveHandler);

        ClickHandler cancelHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                box.hide();
            }
        };
        btnCancel.addClickHandler(cancelHandler);

        // few empty labels to make widget larger
        final Label emptyLabel = new Label("----------");
        emptyLabel.setSize("auto", "25px");
        final HorizontalPanel btnPanel = new HorizontalPanel();
        btnPanel.add(btnSave);
        btnPanel.add(emptyLabel);
        //btnPanel.setCellHorizontalAlignment(btnSave, HasAlignment.ALIGN_RIGHT);
        //btnSave.setWidth("50px");
        btnPanel.add(btnCancel);
        //btnPanel.setCellHorizontalAlignment(btnCancel, HasAlignment.ALIGN_LEFT);
        //btnPanel.setSpacing(40);
        panel.add(btnPanel);
        box.add(panel);
        box.getElement().getStyle().setProperty("zIndex", Integer.toString(9000));
        return box;
    }

    public RemoveTagDialog removeDialogWidget(final String header) {//, String content) {
        final List<Icon> icons = m_mapView.getIconLayer().getIcons();
        if (icons.size() == 0) {
            return null;
        }
        final RemoveTagDialog box = new RemoveTagDialog();
        box.setText(header);

        final VerticalPanel panel = new VerticalPanel();
        final ListBox lb = new ListBox();

        m_nameCount = 0;
        for (Icon icon : icons) {
            String name = icon.getLabelText();
            if (name != null) {
                m_nameCount++;
                lb.addItem(name);
            }
        }
        lb.setVisibleItemCount(m_nameCount);
        panel.add(lb);
        final Button btnDelete = new Button("delete");
        final Button btnExit = new Button("exit");
        ClickHandler deleteHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                for (int j = 0; j < m_nameCount; j++) {
                    if (lb.isItemSelected(j)) {
                        String val = lb.getValue(j);
                        for (Icon icon : icons) {
                            String name = icon.getLabelText();
                            if (name != null && name.compareTo(val) == 0) {
                                m_nameCount -= 1;
                                lb.removeItem(j);
                                m_mapView.getIconLayer().removeIcon(icon);
                                deleteTagFromDisk(name);
                                break;
                            }
                        }
                    }
                }
                m_mapView.updateView();
                m_bTagOn = false;
            }

            private void deleteTagFromDisk(String name) {
                // Initialize the service proxy.
                if (tagControlService == null) {
                    tagControlService = GWT.create(TagControlService.class);
                }
                // Set up the callback object.
                AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Internal Server Error:" + caught.getMessage());
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                    }
                };
                tagControlService.deleteTagFromDisk(name, callback);
            }
        };
        btnDelete.addClickHandler(deleteHandler);

        ClickHandler exitHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                box.hide();
            }
        };
        btnExit.addClickHandler(exitHandler);

        // few empty labels to make widget larger
        final Label emptyLabel = new Label("          ");
        emptyLabel.setSize("auto", "25px");
        final HorizontalPanel btnPanel = new HorizontalPanel();
        btnPanel.add(btnDelete);
        btnPanel.add(emptyLabel);
        btnPanel.add(btnExit);
        panel.add(btnPanel);
        box.add(panel);
        box.getElement().getStyle().setProperty("zIndex", Integer.toString(9000));
        return box;
    }
}
