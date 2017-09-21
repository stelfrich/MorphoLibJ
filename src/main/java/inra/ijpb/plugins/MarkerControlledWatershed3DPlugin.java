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

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import inra.ijpb.binary.BinaryImages;
import inra.ijpb.data.image.Images3D;
import inra.ijpb.watershed.Watershed;


/**
 * 
 * A plugin to perform marker-controlled watershed on a 2D or 3D image.
 * 
 * Reference: Fernand Meyer and Serge Beucher. "Morphological segmentation." 
 * Journal of visual communication and image representation 1.1 (1990): 21-46.
 *
 * @author Ignacio Arganda-Carreras
 */
@Plugin(type = Command.class, menuPath = "Plugins>MorphoLibJ>Segmentation>Marker-controlled Watershed")
public class MarkerControlledWatershed3DPlugin implements Command
{
	@Parameter
	private ImagePlus input;
	
	@Parameter
	private ImagePlus marker;
	
	@Parameter(required = false)
	private ImagePlus mask;
	
	@Parameter(type=ItemIO.OUTPUT)
	private ImagePlus result;
	
	/** flag set to TRUE if markers are binary, to FALSE if markers are labels */
	@Parameter
	private boolean binaryMarkers = true;
	
	/** flag to calculate watershed dams */
	@Parameter
	private boolean getDams = true;
	
	/** flag to use 26-connectivity */
	@Parameter
	private boolean use26neighbors = true;

	@Parameter
	LogService logService;
	
	/**
	 * Apply marker-controlled watershed to a grayscale 2D or 3D image.
	 *	 
	 * @param input grayscale 2D or 3D image (in principle a "gradient" image)
	 * @param marker the labeled marker image
	 * @param mask binary mask to restrict region of interest
	 * @param connectivity 6 or 26 voxel connectivity
	 * @return the resulting watershed
	 */
	@SuppressWarnings( "hiding" )
	public ImagePlus process(
			ImagePlus input, 
			ImagePlus marker,
			ImagePlus mask,
			int connectivity ) 
	{
		final long start = System.currentTimeMillis();
		
		if (binaryMarkers)
		{
			logService.info( "-> Compute marker labels" );
			marker = BinaryImages.componentsLabeling(marker, connectivity, 32);
		}
		
		logService.info("-> Running watershed...");
		
		ImagePlus resultImage = Watershed.computeWatershed(input, marker, mask, connectivity, getDams );
		
		final long end = System.currentTimeMillis();
		logService.info( "Watershed 3d took " + (end-start) + " ms.");
		
		return resultImage;
	}
	

	@Override
	public void run()
	{
		// a 3D image is assumed but it will use 2D connectivity if the
		// input is 2D
		int connectivity = use26neighbors ? 26 : 6;
		if ( input.getImageStackSize() == 1 )
			connectivity = use26neighbors ? 8 : 4;

		result = process( input, marker, mask, connectivity );

		// Set result slice to the current slice in the input image
		result.setSlice( input.getCurrentSlice() );

		// optimize display range
		Images3D.optimizeDisplayRange( result );
	}

}
