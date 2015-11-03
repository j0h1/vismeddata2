package examples;

import org.itk.simple.Image;
import org.itk.simple.SimpleITK;

public class GaussianExample {

    /**
     * @param args
     */
    public static void main(String[] args) {

        if (args.length < 2) {
            System.err.println("Usage: 'Gaussian <input> <output>'");
            System.exit(1);
        }
        System.out.println("Starting to blur " + args[0]);
        // Grab a file
        Image image = SimpleITK.readImage(args[0]);
        Image output = SimpleITK.discreteGaussian(image);
        SimpleITK.writeImage(output, args[1]);
        System.out.println("Finished blurring, writing to " + args[1]);

    }

}