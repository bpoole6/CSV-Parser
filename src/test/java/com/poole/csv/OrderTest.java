package com.poole.csv;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.poole.csv.annotation.CSVColumn;
import com.poole.csv.annotation.CSVComponent;
import com.poole.csv.annotation.CSVReaderType;
import com.poole.csv.exception.OrderParserException;
import com.poole.csv.exception.UninstantiableException;
import com.poole.csv.processor.CSVProcessor;

public class OrderTest {

	@Test()
	public void outOfBoundsNumberFormatExTest() throws IOException {
		CSVProcessor p = new CSVProcessor();
		List<O1> o1s = p.parse(new StringReader("a,j,NAMED,dd"), O1.class);
		O1 o1 = new O1();
		o1.s = "a";
		o1.j = 0;
		o1.type=CSVReaderType.NAMED;
		assertTrue(o1.equals(o1s.get(0)));
	}

	@Test(expected = UninstantiableException.class)
	public void UninstantiableExTest() throws IOException {

		CSVProcessor p = new CSVProcessor();
		List<O2> o1 = p.parse(new StringReader("a,j,NAMED"), O2.class);

	}
	@Test(expected = OrderParserException.class)
	public void repeatedOrderTest() throws IOException {

		CSVProcessor p = new CSVProcessor();
		p.parse(new StringReader("a,j,NAMED"), O4.class);

	}
	@CSVComponent(type = CSVReaderType.ORDER)
	public static class O1 {

		
		@CSVColumn(order = 0)
		String s;
		@CSVColumn(order = 4)
		int i;
		int j;
		@CSVColumn(order=2)
		CSVReaderType type;
		@CSVColumn(order=3)
		char c;
		@CSVColumn(order = 1)
		public void setJ(int j) {
			this.j = j;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + c;
			result = prime * result + i;
			result = prime * result + j;
			result = prime * result + ((s == null) ? 0 : s.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			O1 other = (O1) obj;
			if (c != other.c)
				return false;
			if (i != other.i)
				return false;
			if (j != other.j)
				return false;
			if (s == null) {
				if (other.s != null)
					return false;
			} else if (!s.equals(other.s))
				return false;
			if (type != other.type)
				return false;
			return true;
		}
		

		

	}

	@CSVComponent(type = CSVReaderType.ORDER)
	private static class O2 {
		@CSVColumn(order = 0)
		String s;
		@CSVColumn(order = 1, isNullable = false)
		Integer i;
	}

	@CSVComponent(type = CSVReaderType.ORDER)
	private static class O3 {
		@CSVColumn(order = 0)
		String s;
		@CSVColumn(order = 1, isNullable = false)
		Integer i;
	}
	@CSVComponent(type = CSVReaderType.ORDER)
	private static class O4 {
		@CSVColumn(order = 0)
		String s;
		@CSVColumn(order = 0)
		Integer i;
	}
}
