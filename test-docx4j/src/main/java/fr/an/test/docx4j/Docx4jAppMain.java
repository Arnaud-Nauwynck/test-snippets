package fr.an.test.docx4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.docx4j.openpackaging.packages.OpcPackage;
import org.docx4j.openpackaging.packages.PresentationMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.PresentationML.MainPresentationPart;
import org.docx4j.openpackaging.parts.PresentationML.SlideLayoutPart;
import org.docx4j.openpackaging.parts.PresentationML.SlidePart;
import org.pptx4j.convert.out.svginhtml.SvgExporter;
import org.pptx4j.model.ResolvedLayout;
import org.pptx4j.pml.GroupShape;

public class Docx4jAppMain {

	public static void main(String[] args) throws Exception {
		String fileName = "C:\\arn\\devPerso\\Presentations\\pres-bigdata\\BigData-5-spark-rdd-sql.pptx";
		PresentationMLPackage presentationMLPackage = (PresentationMLPackage) 
				OpcPackage.load(new File(fileName ));

	    HashMap<PartName, Part> parts = presentationMLPackage.getParts().getParts();
		MainPresentationPart pp = (MainPresentationPart)parts.get(
	            new PartName("/ppt/presentation.xml"));

		int slideIndex = 0;
	    List<SlidePart> slideParts = pp.getSlideParts();
	    for(SlidePart slidePart : slideParts) {
	    	slideIndex++;
	    	System.out.println("slide:" + slidePart);
	    	
//	    	ResolvedLayout rl = slidePart.getResolvedLayout();
//	    	GroupShape slideGroupShape = rl.getShapeTree();
//	    	System.out.println("slideGroupShape: " + slideGroupShape);
	    	
	    	String slideSvg = SvgExporter.svg(presentationMLPackage, slidePart);
	    	System.out.println("slide SVG: " + slideSvg);
	    }
	    

	}
}
