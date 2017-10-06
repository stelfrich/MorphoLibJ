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

import net.imagej.ops.OpService;
import net.imagej.ops.image.watershed.WatershedSeeded;
import net.imglib2.algorithm.labeling.ConnectedComponents.StructuringElement;
import net.imglib2.img.Img;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.type.BooleanType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;

import org.scijava.ItemIO;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;

/**
 * 
 * A plugin to perform marker-controlled watershed on a 2D or 3D image.
 * 
 * Reference: Fernand Meyer and Serge Beucher. "Morphological segmentation." 
 * Journal of visual communication and image representation 1.1 (1990): 21-46.
 *
 * @author Ignacio Arganda-Carreras
 */
public abstract class AbstractMarkerControlledWatershed<I extends RealType<I>, L, LB extends IntegerType<LB>, M extends BooleanType<M>>
{
	@Parameter
	Img<I> input;
	
	@Parameter(required = false)
	Img<M> mask;
	
	@Parameter(type=ItemIO.OUTPUT)
	ImgLabeling<L, LB> result;
	
	/** flag to calculate watershed dams */
	@Parameter
	boolean getDams = true;
	
	/** flag to use 26-connectivity */
	@Parameter
	boolean use26neighbors = true;

	@Parameter
	LogService logService;
	
	@Parameter
	OpService opService;
	
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
	public ImgLabeling<L, LB> process(
			Img<I> input, 
			ImgLabeling<L, LB> marker,
			Img<M> mask,
			StructuringElement connectivity ) 
	{
		final long start = System.currentTimeMillis();
		
		logService.info("-> Running watershed...");
	
		ImgLabeling<L, LB> out = ( ImgLabeling< L, LB > ) opService.run(WatershedSeeded.class, null,
				input, marker, true, getDams, mask);
		
		final long end = System.currentTimeMillis();
		logService.info( "Watershed 3d took " + (end-start) + " ms.");
		
		return out;
	}

}
