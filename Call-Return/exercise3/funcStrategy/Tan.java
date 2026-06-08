package funcStrategy;
public class Tan implements FuncStrategy {
    @Override
    public double execute(double number) {
        return Math.tan(number);
    }

    @Override
    public String getName() {
        return "Tan";
    }
}
