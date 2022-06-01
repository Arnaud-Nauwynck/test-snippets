package fr.an.test.aspose.slides;

import com.aspose.slides.AutoShape;
import com.aspose.slides.IShape;
import com.aspose.slides.IShapeCollection;
import com.aspose.slides.ISlide;
import com.aspose.slides.ISlideCollection;
import com.aspose.slides.ITextFrame;
import com.aspose.slides.Presentation;

public class AsposeSlideAppMain {

	public static void main(String[] args) {
		Presentation pres = new Presentation("presentation.pptx");
		ISlideCollection slides = pres.getSlides();
		int slideCount = slides.size();
		for(int i = 0; i < slideCount; i++) {
			ISlide slide = slides.get_Item(i);
			System.out.println("slide[" + i + "]: " +  slide);
			
			IShapeCollection shapes = slide.getShapes();
			int shapeCount = shapes.size();
			for(int shapeIdx = 0; shapeIdx < shapeCount; shapeIdx++) {
				IShape shape = shapes.get_Item(shapeIdx);
				if (shape instanceof AutoShape) {
					AutoShape shape2 = (AutoShape) shape;
					ITextFrame textFrame = shape2.getTextFrame();
					System.out.println("autoShape textFrame:" +  textFrame.getText());
				} else {
					System.out.println("shape:" +  shape);
				}
			}
		}
	}
}
