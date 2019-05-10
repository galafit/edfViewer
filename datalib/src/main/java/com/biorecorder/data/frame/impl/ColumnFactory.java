package com.biorecorder.data.frame.impl;

import com.biorecorder.data.frame.Column;
import com.biorecorder.data.frame.DataType;
import com.biorecorder.data.frame.Function;
import com.biorecorder.data.sequence.*;
import com.biorecorder.data.utils.PrimitiveUtils;


public class ColumnFactory {
    public static Column createColumn(ShortSequence data) {
        return new ShortColumn(data);
    }
    public static Column createColumn(IntSequence data) {
        return new IntColumn(data);
    }
    public static Column createColumn(LongSequence data) {
        return new LongColumn(data);
    }
    public static Column createColumn(FloatSequence data) {
        return new FloatColumn(data);
    }
    public static Column createColumn(DoubleSequence data) {
        return new DoubleColumn(data);
    }
    public static Column createColumn(StringSequence data) {
        return new StringColumn(data);
    }
    public static Column createColumn(double start, double step) {
        return new RegularColumn(start, step);
    }
    public static Column createColumn(double start, double step, int size) {
        return new RegularColumn(start, step, size);
    }
    public static Column createColumn(Function function, Column argColumn) {
        return new FunctionColumn(function, argColumn);
    }

   public static Column concat(Column column1, int column1Length, Column column2) {
        if (column1.isRegular() && column2.isRegular()) {
            RegularColumn regColumn1 = (RegularColumn) column1;
            RegularColumn regColumn2 = (RegularColumn) column2;
            if (regColumn1.getStep() == regColumn2.getStep() && regColumn2.getStartValue() == regColumn1.value(column1Length)) {
                long size1 = column1Length;
                long size2 = column2.size();
                return new RegularColumn(regColumn1.getStartValue(), regColumn1.getStep(), PrimitiveUtils.long2int(size1 + size2));
            }
        }

        if (column1.dataType() == DataType.String || column1.dataType() == DataType.String) {
            StringSequence resultantSequence = new StringSequence() {
                @Override
                public int size() {
                    return column1Length + column2.size();
                }

                @Override
                public String get(int index) {
                    if (index < column1Length) {
                        return column1.label(index);
                    } else {
                        return column2.label(index - column1Length);
                    }
                }
            };
            return new StringColumn(resultantSequence);
        }

        if(column1.dataType() == column2.dataType()) {
            switch (column1.dataType()) {
                case Short:
                    return concat((ShortColumn) column1, column1Length, (ShortColumn) column1);
                case Integer:
                    return concat((IntColumn) column1, column1Length, (IntColumn) column1);
                case Long:
                    return concat((LongColumn) column1, column1Length, (LongColumn) column1);
                case Float:
                    return concat((FloatColumn) column1, column1Length, (FloatColumn) column1);
            }
        }

        DoubleSequence resultantSequence = new DoubleSequence() {
            @Override
            public int size() {
                return column1Length + column2.size();
            }

            @Override
            public double get(int index) {
                if(index < column1Length) {
                    return column1.value(index);
                } else {
                    return column2.value(index - column1Length);
                }
            }
        };
        return new DoubleColumn(resultantSequence);
   }

   private static Column concat(DoubleColumn column1, int column1Length, DoubleColumn column2) {
        DoubleSequence resultantSequence = new DoubleSequence() {
            @Override
            public int size() {
                return column1Length + column2.size();
            }

            @Override
            public double get(int index) {
                if(index < column1Length) {
                    return column1.doubleValue(index);
                } else {
                    return column2.doubleValue(index - column1Length);
                }
            }
        };
        return new DoubleColumn(resultantSequence);
   }

   private static Column concat(FloatColumn column1, int column1Length, FloatColumn column2) {
        FloatSequence resultantSequence = new FloatSequence() {
            @Override
            public int size() {
                return column1Length + column2.size();
            }

            @Override
            public float get(int index) {
                if(index < column1Length) {
                    return column1.floatValue(index);
                } else {
                    return column2.floatValue(index - column1Length);
                }
            }
        };
        return new FloatColumn(resultantSequence);
   }

   private static Column concat(IntColumn column1, int column1Length, IntColumn column2) {
        IntSequence resultantSequence = new IntSequence() {
            @Override
            public int size() {
                return column1Length + column2.size();
            }

            @Override
            public int get(int index) {
                if(index < column1Length) {
                    return column1.intValue(index);
                } else {
                    return column2.intValue(index - column1Length);
                }
            }
        };
        return new IntColumn(resultantSequence);
   }

   private static Column concat(LongColumn column1, int column1Length, LongColumn column2) {
        LongSequence resultantSequence = new LongSequence() {
            @Override
            public int size() {
                return column1Length + column2.size();
            }

            @Override
            public long get(int index) {
                if(index < column1Length) {
                    return column1.longValue(index);
                } else {
                    return column2.longValue(index - column1Length);
                }
            }
        };
        return new LongColumn(resultantSequence);
   }

   private static Column concat(ShortColumn column1, int column1Length, ShortColumn column2) {
        ShortSequence resultantSequence = new ShortSequence() {
            @Override
            public int size() {
                return column1Length + column2.size();
            }

            @Override
            public short get(int index) {
                if(index < column1Length) {
                    return column1.shortValue(index);
                } else {
                    return column2.shortValue(index - column1Length);
                }
            }
        };
        return new ShortColumn(resultantSequence);
   }
}
