/*-
 * #%L
 * Mathematical morphology library and plugins for ImageJ/Fiji.
 * %%
 * Copyright (C) 2014 - 2017 INRA.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package inra.ijpb.plugins;


import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import inra.ijpb.algo.DefaultAlgoListener;
import inra.ijpb.morphology.Morphology;
import inra.ijpb.morphology.Morphology.Operation;
import inra.ijpb.morphology.Strel;

import java.awt.AWTEvent;

import net.imagej.autoscale.AutoscaleService;
import net.imagej.display.DatasetView;
import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.neighborhood.RectangleShape;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.command.DynamicCommand;
import org.scijava.command.InteractiveCommand;
import org.scijava.command.Previewable;
import org.scijava.display.Display;
import org.scijava.display.DisplayService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

/**
 * Plugin for computing various morphological filters on gray scale or color
 * images.
 *
 * @author David Legland
 *
 */
@Plugin(type=Command.class, menuPath="Plugins>MorphoLibJ>Morphological Filters (Ops)")
public class MorphologicalFilters<T extends RealType<T>> extends DynamicCommand implements Previewable
{
	
	PlugInFilterRunner pfr;
	int nPasses;
	
	@Parameter(label="Show preview", callback = "previewChanged")
	boolean showPreview = false;
	
	/** need to keep the instance of ImagePlus */
	@Parameter
	private Img<T> imagePlus;
	
	/** keep the original image, to restore it after the preview */
	private ImageProcessor baseImage;
	
	/** Keep instance of result image */
	@Parameter(type=ItemIO.OUTPUT)
	private ImageProcessor result;

	/** an instance of ImagePlus to display the Strel */
	private Display<?> strelDisplay = null;
	
	@Parameter
	UIService uiService;
	
	@Parameter
	private AutoscaleService autoscaleService;

	@Parameter(type = ItemIO.BOTH)
	private DatasetView view;
	
	@Parameter(label="Operation", callback = "operationChanged", choices = {"Erosion", "Dilation"})
	String op = "Erosion";
	
	@Parameter(label="Element", choices = {"Disk", "Square"}, callback = "elementChanged")
	String shape = "Disk";
	
	@Parameter(label="Radius (in pixels)")
	int radius = 2;
	
	@Parameter(label="Show Element")
	boolean showStrel;
	
	@Parameter
	private OpService opService;
	
	private Display<?> previewDisplay;
	
	@Parameter
	DisplayService displayService;
	
	/**
	 * Setup function is called in the beginning of the process, but also at the
	 * end. It is also used for displaying "about" frame.
	 */
//	public int setup(String arg, ImagePlus imp) 
//	{
//		// about...
//		if (arg.equals("about")) 
//		{
//			showAbout(); 
//			return DONE;
//		}
//
//		// Called at the end for cleaning the results
//		if (arg.equals("final")) 
//		{
//			// replace the preview image by the original image 
//			resetPreview();
//			imagePlus.updateAndDraw();
//	    	
//			// Create a new ImagePlus with the filter result
//			String newName = createResultImageName(imagePlus);
//			ImagePlus resPlus = new ImagePlus(newName, result);
//			resPlus.copyScale(imagePlus);
//			resPlus.show();
//			return DONE;
//		}
//		
//		return flags;
//	}
	
	
//	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr)
//	{
//		// Normal setup
//    	this.imagePlus = imp;
//    	this.baseImage = imp.getProcessor().duplicate();
//
//		// Create the configuration dialog
//		GenericDialog gd = new GenericDialog("Morphological Filter");
//		
//		gd.addChoice("Operation", Operation.getAllLabels(), 
//				this.op.toString());
//		gd.addChoice("Element", Strel.Shape.getAllLabels(), 
//				this.shape.toString());
//		gd.addPreviewCheckbox(pfr);
//		gd.addDialogListener(this);
//        previewing = true;
//		gd.addHelp("http://imagej.net/MorphoLibJ#Morphological_filters");
//        gd.showDialog();
//        previewing = false;
//        
//        if (gd.wasCanceled()) 
//        {
//        	resetPreview();
//        	return DONE;
//        }	
//        
//    	parseDialogParameters(gd);
//			
//		// clean up an return 
//		gd.dispose();
//		return flags;
//	}
//	
//    public boolean dialogItemChanged(GenericDialog gd, AWTEvent evt)
//    {
//    	boolean wasPreview = this.previewing;
//    	parseDialogParameters(gd);
//    	
//    	// if preview checkbox was unchecked, replace the preview image by the original image
//    	if (wasPreview && !this.previewing)
//    	{
//    		resetPreview();
//    	}
//    	return true;
//    }
//
//    private void parseDialogParameters(GenericDialog gd) 
//    {
//		// extract chosen parameters
//		this.op 		= Operation.fromLabel(gd.getNextChoice());
//		this.shape 		= Strel.Shape.fromLabel(gd.getNextChoice());
//		this.previewing = gd.getPreviewCheckbox().getState();
//    }
//    
//    public void setNPasses (int nPasses) 
//    {
//    	this.nPasses = nPasses;
//    }
    
	@Override
	public void run()
	{
		// Create structuring element of the given size
//		Strel strel = shape.fromRadius(radius);
		
//		// add some listeners
//		DefaultAlgoListener.monitor(strel);
//		
//		// Eventually display the structuring element used for processing 
//		if (showStrel) 
//		{
//			showStrelImage(strel);
		}
		
//		Strel.Shape.DISK;
		
//		opService.run(Ops.Morphology.Erode.class, args);
		
		// Execute core of the plugin on the original image
//		Operation.DILATION;
//		result = op.apply(this.baseImage, strel);
//		if (!(result instanceof ColorProcessor))
//			result.setLut(this.baseImage.getLut());

//    	if (previewing) 
//    	{
//    		// Fill up the values of original image with values of the result
//    		for (int i = 0; i < image.getPixelCount(); i++)
//    		{
//    			image.setf(i, result.getf(i));
//    		}
//    		image.resetMinAndMax();
//        }
//	}
	
//	// About...
//	private void showAbout()
//	{
//		IJ.showMessage("Morphological Filters",
//				"MorphoLibJ,\n" +
//				"http://imagej.net/MorphoLibJ#Morphological_filters\n" +
//				"\n" +
//				"by David Legland\n" +
//				"(david.legland@nantes.inra.fr)");
//	}

//	private void resetPreview()
//	{
//		ImageProcessor image = this.imagePlus.getProcessor();
//		if (image instanceof FloatProcessor)
//		{
//			for (int i = 0; i < image.getPixelCount(); i++)
//				image.setf(i, this.baseImage.getf(i));
//		}
//		else
//		{
//			for (int i = 0; i < image.getPixelCount(); i++)
//				image.set(i, this.baseImage.get(i));
//		}
//		imagePlus.updateAndDraw();
//	}
	
//	@Override
//	public void preview() {
//		// TODO Called everytime a parameter changes
//	}
	
	/**
	 * Displays the current structuring element in a new ImagePlus. 
	 * @param strel the structuring element to display
	 */
	private void showStrelImage(Strel strel)
	{
		// Size of the strel image (little bit larger than strel)
		int[] dim = strel.getSize();
		int width = dim[0] + 20; 
		int height = dim[1] + 20;
		
		// Creates strel image by dilating a point
		ImageProcessor strelImage = new ByteProcessor(width, height);
		strelImage.set(width / 2, height / 2, 255);
		strelImage = Morphology.dilation(strelImage, strel);
		
		// Forces the display to inverted LUT (display a black over white)
		if (!strelImage.isInvertedLut())
			strelImage.invertLut();
		
		// Display strel image
		if (strelDisplay == null)
		{
//			strelDisplay = displayService.createDisplay(new ImagePlus("Structuring Element", strelImage));
		} 
		else 
		{
			((ImagePlus) strelDisplay.get(0)).setProcessor(strelImage);
			strelDisplay.update();
		}
	}

	/**
	 * Applies the specified morphological operation with specified structuring
	 * element to the input image.
	 * 
	 * @param image
	 *            the input image (grayscale or color)
	 * @param op
	 *            the operation to apply
	 * @param strel
	 *            the structuring element to use for the operation
	 * @return the result of morphological operation applied to the input image
	 * @deprecated use the process method instead
	 */
	@Deprecated
	public ImagePlus exec(ImagePlus image, Operation op, Strel strel)
	{
		// Check validity of parameters
		if (image == null)
			return null;
		
		// extract the input processor
		ImageProcessor inputProcessor = image.getProcessor();
		
		// apply morphological operation
		ImageProcessor resultProcessor = op.apply(inputProcessor, strel);
		
		// Keep same color model
		resultProcessor.setColorModel(inputProcessor.getColorModel());
		
		// create the new image plus from the processor
		ImagePlus resultImage = new ImagePlus(op.toString(), resultProcessor);
		resultImage.copyScale(image);
					
		// return the created array
		return resultImage;
	}
	
	/**
	 * Applies the specified morphological operation with specified structuring
	 * element to the input image.
	 * 
	 * @param image
	 *            the input image (grayscale or color)
	 * @param op
	 *            the operation to apply
	 * @param strel
	 *            the structuring element to use for the operation
	 * @return the result of morphological operation applied to the input image
	 */
	public ImagePlus process(ImagePlus image, Operation op, Strel strel)
	{
		// Check validity of parameters
		if (image == null)
			return null;
		
		// extract the input processor
		ImageProcessor inputProcessor = image.getProcessor();
		
		// apply morphological operation
		ImageProcessor resultProcessor = op.apply(inputProcessor, strel);
		
		// Keep same color model
		resultProcessor.setColorModel(inputProcessor.getColorModel());
		
		// create the new image plus from the processor
		ImagePlus resultImage = new ImagePlus(op.toString(), resultProcessor);
		resultImage.copyScale(image);
					
		// return the created array
		return resultImage;
	}
	
	/**
	 * Creates the name for result image, by adding a suffix to the base name
	 * of original image.
	 */
	private String createResultImageName(ImagePlus baseImage) 
	{
		return baseImage.getShortTitle() + "-" + op.toString();
	}
	
	/** Called when view changes. Updates everything to match. */
//	protected void viewChanged() {
//		RandomAccessibleInterval<? extends RealType<?>> plane = view.xyPlane();
//		else interval = view.getData().getImgPlus();
//		
//		computeDataMinMax(interval);
//		computeInitialMinMax();
//		if (Double.isNaN(min)) min = initialMin;
//		if (Double.isNaN(max)) max = initialMax;
//		computeBrightnessContrast();
//		// TEMP : try this to clear up refresh problem
//		// NOPE
//		// updateDisplay();
//	}
	
	/**
	 * TODO Documentation
	 */
	private void elementChanged() {
		view.setChannelRanges(0, 1);
		view.getProjector().map();
		view.update();
//		if (previewing) {
//			RandomAccessibleInterval<? extends RealType<?>> plane = view.xyPlane();
//			// TODO Apply op with new element to plane
//		}
	}
	
	/**
	 * TODO Documentation
	 */
	private void operationChanged() {
		view.setChannelRanges(0, 100);
		view.getProjector().map();
		view.update();
//		if (previewing) {
//			RandomAccessibleInterval<? extends RealType<?>> plane = view.xyPlane();
//			// TODO Apply new op with element to plane
//		}
	}
	
	@Override
	public void cancel() {
		// Set the image's title back to the original value.
		previewDisplay.close();
	}

	private int counter = 1;
	
	public void somethingChanged() {
		RandomAccessibleInterval<? extends RealType<?>> plane = view.xyPlane();
		
		RandomAccessibleInterval copy = opService.copy().rai(plane);
		IterableInterval eroded = opService.morphology().erode(copy, new RectangleShape(counter++, true));
		
		if (previewDisplay == null) {
			previewDisplay = displayService.createDisplay("Preview", eroded);
		} else {
			previewDisplay.clear();
			previewDisplay.display(eroded);
			previewDisplay.update();
		}
		
	}
}
