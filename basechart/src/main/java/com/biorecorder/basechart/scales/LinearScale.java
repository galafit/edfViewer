package com.biorecorder.basechart.scales;

import com.biorecorder.basechart.axis.LabelPrefixAndSuffix;
import com.biorecorder.data.utils.NormalizedNumber;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Arrays;

/**
 * Created by galafit on 6/9/17.
 */
public class LinearScale implements Scale {
    DecimalFormat numberFormatter;
    private double domain[] = {0, 1};
    private double range[] = {0, 1};

    @Override
    public Scale copy() {
        LinearScale copyScale = new LinearScale();
        copyScale.domain = Arrays.copyOf(domain, domain.length);
        copyScale.range = Arrays.copyOf(range, range.length);
        return copyScale;
    }

    @Override
    public void setDomain(double... domain) {
        this.domain = Arrays.copyOf(domain, domain.length);
        numberFormatter = null;
    }

    @Override
    public void setRange(double... range) {
        this.range = Arrays.copyOf(range, range.length);
        numberFormatter = null;
    }

    @Override
    public double[] getDomain() {
        return Arrays.copyOf(domain, domain.length);
    }

    @Override
    public double[] getRange() {
        return Arrays.copyOf(range, range.length);
    }

    @Override
    public double scale(double value) {
        return (range[0] + (value - domain[0]) * (range[range.length - 1] - range[0]) / (domain[domain.length - 1] - domain[0]));
    }

    @Override
    public double invert(double value) {
        return domain[0] + (value - range[0]) * (domain[domain.length - 1] - domain[0]) / (range[range.length - 1] - range[0]);
    }

    @Override
    public TickProvider getTickProviderByIntervalCount(int tickIntervalCount, LabelPrefixAndSuffix formatInfo) {
        LinearTickProvider provider = new LinearTickProvider(formatInfo);
        provider.setTickIntervalCount(tickIntervalCount);
        return provider;
    }

    @Override
    public TickProvider getTickProviderByInterval(double tickInterval, LabelPrefixAndSuffix formatInfo) {
        LinearTickProvider provider = new LinearTickProvider(formatInfo);
        provider.setTickInterval(tickInterval);
        return provider;
    }

    @Override
    public String formatDomainValue(double value) {
        if (numberFormatter == null) {
            float rangePointsCount = (int)Math.abs(range[range.length - 1] - range[0]) + 1;
            double domainLength = domain[domain.length - 1] - domain[0];
            double pointInterval = domainLength / rangePointsCount;
            numberFormatter = getNumberFormat(NormalizedNumber.firstDigitExponent(pointInterval), null);
        }
        long longPart = (long) value;
        if(value == longPart) {
            return String.valueOf(longPart);
        }
        return numberFormatter.format(value);
    }

    // TODO: use metric shortcuts - k, M, G... from formatInfo
    private DecimalFormat getNumberFormat(int power, LabelPrefixAndSuffix labelFormatInfo) {
        DecimalFormat dfNeg4 = new DecimalFormat("0.0000");
        DecimalFormat dfNeg3 = new DecimalFormat("0.000");
        DecimalFormat dfNeg2 = new DecimalFormat("0.00");
        DecimalFormat dfNeg1 = new DecimalFormat("0.0");
        DecimalFormat df0 = new DecimalFormat("#,##0");

        DecimalFormat df = new DecimalFormat("#.######E0");

        if (power == -4) {
            df = dfNeg4;
        }
        if (power == -3) {
            df = dfNeg3;
        }
        if (power == -2) {
            df = dfNeg2;
        }
        if (power == -1) {
            df = dfNeg1;
        }
        if (power >= 0 && power <= 6) {
            df = df0;
        }

        String formatPattern = df.toPattern();
        if (labelFormatInfo != null && labelFormatInfo.getPrefix() != null) {
            formatPattern = labelFormatInfo.getPrefix() + " " + formatPattern;
        }
        if (labelFormatInfo != null && labelFormatInfo.getSuffix() != null) {
            formatPattern = formatPattern + " " + labelFormatInfo.getSuffix();

        }
        df = new DecimalFormat(formatPattern);
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df;
    }


    class LinearTickProvider implements TickProvider {
        NormalizedNumber tickInterval;
        private LabelPrefixAndSuffix labelFormatInfo;
        private DecimalFormat labelFormat = new DecimalFormat();
        private long currentTickNumber;

        public LinearTickProvider(LabelPrefixAndSuffix labelFormatInfo) {
            this.labelFormatInfo = labelFormatInfo;
        }

        public void setTickInterval(double tickInterval) {
            this.tickInterval = new NormalizedNumber(tickInterval);
            labelFormat = getNumberFormat(this.tickInterval.exponent(), labelFormatInfo);
        }

        /**
         * Calculates round Tick Interval
         * that is  multiples of 2, 5 or 10.
         * FirstDigit is in {1,2,5,10};
         */
        public void setTickIntervalCount(int tickIntervalCount) {
            if (tickIntervalCount < 1) {
                String errMsg = MessageFormat.format("Invalid tick interval count: {0}. Expected >= 2", tickIntervalCount);
                throw new IllegalArgumentException(errMsg);
            }
            double max = domain[domain.length - 1];
            double min = domain[0];
            double interval = (max - min) / tickIntervalCount;

            int exponent = NormalizedNumber.firstDigitExponent(interval);
            int firstDigit = (int) Math.round(interval / Math.pow(10, exponent));
            switch (firstDigit) {
                case 3:
                    firstDigit = 2;
                    break;
                case 4:
                    firstDigit = 5;
                    break;
                case 6:
                    firstDigit = 5;
                    break;
                case 7:
                    firstDigit = 5;
                    break;
                case 8:
                    firstDigit = 1;
                    exponent++;
                    break;
                case 9:
                    firstDigit = 1;
                    exponent++;
                    break;
                case 10:
                    firstDigit = 1;
                    exponent++;
                    break;
            }
            tickInterval = new NormalizedNumber(firstDigit, exponent);
            labelFormat = getNumberFormat(exponent, labelFormatInfo);
        }

        @Override
        public void increaseTickInterval(int increaseFactor) {
            tickInterval = tickInterval.multiply(increaseFactor);
            labelFormat = getNumberFormat(tickInterval.exponent(), labelFormatInfo);
        }

        @Override
        public Tick getNextTick() {
            currentTickNumber++;
            NormalizedNumber tickValue = tickInterval.multiply(currentTickNumber);
            return new Tick(tickValue, format(tickValue.value()));
        }

        @Override
        public Tick getPreviousTick() {
            currentTickNumber--;
            NormalizedNumber tickValue = tickInterval.multiply(currentTickNumber);
            return new Tick(tickValue, format(tickValue.value()));
        }

        @Override
        public Tick getUpperTick(double value) {
            currentTickNumber = tickInterval.upperScaleFactor(value);
            NormalizedNumber tickValue = new NormalizedNumber(currentTickNumber * tickInterval.mantissaDigits(), tickInterval.exponent());
            return new Tick(tickValue, format(tickValue.value()));
        }

        @Override
        public Tick getLowerTick(double value) {
            currentTickNumber = tickInterval.lowerScaleFactor(value);
            NormalizedNumber tickValue = new NormalizedNumber(currentTickNumber * tickInterval.mantissaDigits(), tickInterval.exponent());
            return new Tick(tickValue, format(tickValue.value()));
        }

        public String format(double value) {
            String formattedValue = labelFormat.format(value);
            // truncate the negative sign when the result returns zero: -0.0 and so on
            formattedValue = formattedValue.replaceAll("^-(?=0(.0*)?$)", "");
            return formattedValue;
        }

    }
}
