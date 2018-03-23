package com.vp.plugin.sample.imagemap.actions;

import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ModelConvertionManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.IShapeUIModel;

public class ImageMapActionControl implements VPActionController {

	@Override
	public void performAction(VPAction arg0) {
		// Create file chooser for picking the output directory
		JFileChooser fileChooser = ApplicationManager.instance().getViewManager().createJFileChooser();
		fileChooser.setDialogTitle("Specify output folder");
		fileChooser.setDialogType(JFileChooser.DIRECTORIES_ONLY);
		int returnValue = fileChooser.showSaveDialog(null);
		
		// if user selected a folder then proceed to genrate the image map
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File outputFolder = fileChooser.getSelectedFile();
			outputFolder.mkdirs();
			
			// Create point object to retrieve the trimmed offset between actual diagram and exported diagram 
			Point offsetPoint = new Point();
			
			// Obtain the ModelConvertionManager
			ModelConvertionManager convertionManager = ApplicationManager.instance().getModelConvertionManager(); 
			
			// Ask ModelConvertionManager to export active diagram into SVG image to the output folder.
			// The Point object will filled with offset value after export diagram to image. 
			convertionManager.exportActiveDiagramAsImage(new File(outputFolder.getAbsolutePath() + File.separator + "image.png"), 
															ModelConvertionManager.IMAGE_TYPE_PNG, offsetPoint);
			
			// Start generate HTML with image map
			generateImageMap(outputFolder, ApplicationManager.instance().getDiagramManager().getActiveDiagram(), offsetPoint);
			
			// Display a message indicate the process is done.
			ApplicationManager.instance().getViewManager().showMessage("Image map generated to " + outputFolder.getAbsolutePath());
		}
		
	}
	
	private void generateImageMap(File outputPath, IDiagramUIModel diagram, Point offsetPoint) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html><head></head><body>\n");
		sb.append("<img src=\"image.png\" usemap=\"#imgmap\"/>\n");
		
		sb.append("<map name=\"imgmap\">\n");
		
		// Loop through all shapes in active diagram
		IShapeUIModel[] shapes = diagram.toShapeUIModelArray();
		if (shapes != null && shapes.length > 0) {
			for (IShapeUIModel shape : shapes) {
				// Create a map area for each shape. 
				// Remember to reduce the trimmed offset when export diagram to image 
				sb.append("<area shape=\"rect\" coords=\"" + (shape.getX() - offsetPoint.getX()) 
														   + "," + (shape.getY() - offsetPoint.getY()) 
														   + "," + (shape.getX() + shape.getWidth() - offsetPoint.getX()) 
														   + "," + (shape.getY() + shape.getHeight() - offsetPoint.getY()) 
														   + "\" href=\"https://www.visual-paradigm.com\">\n");
			}
		}
		// Close the image map and HTML		
		sb.append("</map>\n");		
		sb.append("</body></html>");
		
		// Write the HTML with image map to file 
		File htmlFile = new File(outputPath + File.separator + "index.html");
		try {
			FileOutputStream fout = new FileOutputStream(htmlFile);
			fout.write(sb.toString().getBytes());
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub
		
	}

}
