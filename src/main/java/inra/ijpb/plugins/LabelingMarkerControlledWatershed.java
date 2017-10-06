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

import net.imglib2.algorithm.labeling.ConnectedComponents.StructuringElement;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.type.BooleanType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;

import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * 
 * A plugin to perform marker-controlled watershed on a 2D or 3D image.
 * 
 * Reference: Fernand Meyer and Serge Beucher. "Morphological segmentation." 
 * Journal of visual communication and image representation 1.1 (1990): 21-46.
 *
 * @author Ignacio Arganda-Carreras
 */
@Plugin(type = Command.class, menuPath = "Plugins>MorphoLibJ>Segmentation>Marker-controlled Watershed (Labeled Seeds)")
public class LabelingMarkerControlledWatershed<I extends RealType<I>, L, LB extends IntegerType<LB>, M extends BooleanType<M>> extends AbstractMarkerControlledWatershed< I, L, LB, M> implements Command
{
	
	@Parameter
	private ImgLabeling<L, LB> marker;
	
	@Override
	public void run()
	{
		StructuringElement connectivity = use26neighbors ? StructuringElement.EIGHT_CONNECTED : StructuringElement.FOUR_CONNECTED;

		result = process( input, marker, mask, connectivity );

		// Set result slice to the current slice in the input image
		// FIXME
//		result.setSlice( input.getCurrentSlice() );

		// optimize display range
		// FIXME
//		Images3D.optimizeDisplayRange( result );
	}

}
