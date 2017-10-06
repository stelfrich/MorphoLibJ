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


import java.io.IOException;

import net.imagej.Dataset;
import net.imagej.ImageJ;


public class MorphologicalFiltersMain
{
	
	public static void main(String[] args) {
		ImageJ ij = new ImageJ();
		ij.ui().showUI();
		
		try {
			Dataset ds = (Dataset) ij.io().open("/home/stefan/Desktop/TestOverlay.tif");
			ij.ui().show(ds);
			
			ij.command().run(MorphologicalFilters.class, true);
		}
		catch (IOException exc) {
			// TODO Auto-generated catch block
			exc.printStackTrace();
		}
	}

}
