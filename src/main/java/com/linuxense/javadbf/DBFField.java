/*

(C) Copyright 2015 Alberto Fernández <infjaf@gmail.com>
(C) Copyright 2014 Jan Schlößin
(C) Copyright 2003-2004 Anil Kumar K <anil@linuxense.com>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3.0 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library.  If not, see <http://www.gnu.org/licenses/>.

*/

package com.linuxense.javadbf;
import java.io.*;

/*
DBFField
	Class represents a "field" (or column) definition of a DBF data structure.

*/

/**
	DBFField represents a field specification in an dbf file.

	DBFField objects are either created and added to a DBFWriter object or obtained
	from DBFReader object through getField( int) query.

*/
public class DBFField {

	public static final byte FIELD_TYPE_C = (byte)'C';
	public static final byte FIELD_TYPE_L = (byte)'L';
	public static final byte FIELD_TYPE_N = (byte)'N';
	public static final byte FIELD_TYPE_F = (byte)'F';
	public static final byte FIELD_TYPE_D = (byte)'D';
	public static final byte FIELD_TYPE_M = (byte)'M';

	/* Field struct variables start here */
	byte[] fieldName = new byte[ 11]; /* 0-10*/
	byte dataType;                    /* 11 */
	int reserv1;                      /* 12-15 */
	int fieldLength;                 /* 16 */
	byte decimalCount;                /* 17 */
	short reserv2;                    /* 18-19 */
	byte workAreaId;                  /* 20 */
	short reserv3;                    /* 21-22 */
	byte setFieldsFlag;               /* 23 */
	byte[] reserv4 = new byte[ 7];    /* 24-30 */
	byte indexFieldFlag;              /* 31 */
	/* Field struct variables end here */

	/* other class variables */
	int nameNullIndex = 0;

	/**
	Creates a DBFField object from the data read from the given DataInputStream.

	The data in the DataInputStream object is supposed to be organised correctly
	and the stream "pointer" is supposed to be positioned properly.

	@param in DataInputStream
	@return Returns the created DBFField object.
	@throws IOException If any stream reading problems occures.
	*/
	protected static DBFField createField( DataInput in) 
	throws IOException {

		DBFField field = new DBFField();

		byte t_byte = in.readByte(); /* 0 */
		if( t_byte == (byte)0x0d) {

			//System.out.println( "End of header found");
			return null;
		}

		in.readFully( field.fieldName, 1, 10);	/* 1-10 */
		field.fieldName[0] = t_byte;

		for( int i=0; i<field.fieldName.length; i++) {

			if( field.fieldName[ i] == (byte)0) {

				field.nameNullIndex = i;
				break;
			}
		}

		field.dataType = in.readByte(); /* 11 */
		field.reserv1 = Utils.readLittleEndianInt( in); /* 12-15 */
		field.fieldLength = in.readUnsignedByte();  /* 16 */
		field.decimalCount = in.readByte(); /* 17 */
		field.reserv2 = Utils.readLittleEndianShort( in); /* 18-19 */
		field.workAreaId = in.readByte(); /* 20 */
		field.reserv2 = Utils.readLittleEndianShort( in); /* 21-22 */
		field.setFieldsFlag = in.readByte(); /* 23 */
		in.readFully( field.reserv4); /* 24-30 */
		field.indexFieldFlag = in.readByte(); /* 31 */

		return field;
	}

	/**
		Writes the content of DBFField object into the stream as per
		DBF format specifications.

		@param out OutputStream
		@throws IOException if any stream related issues occur.
	*/
	protected void write( DataOutput out)
	throws IOException {

		//DataOutputStream out = new DataOutputStream( os);

		// Field Name
		out.write( fieldName);        /* 0-10 */
		out.write( new byte[ 11 - fieldName.length]);

		// data type
		out.writeByte( dataType); /* 11 */
		out.writeInt( 0x00);   /* 12-15 */
		out.writeByte( fieldLength); /* 16 */
		out.writeByte( decimalCount); /* 17 */
		out.writeShort( (short)0x00); /* 18-19 */
		out.writeByte( (byte)0x00); /* 20 */
		out.writeShort( (short)0x00); /* 21-22 */
		out.writeByte( (byte)0x00); /* 23 */
		out.write( new byte[7]); /* 24-30*/
		out.writeByte( (byte)0x00); /* 31 */
	}

	/**
		Returns the name of the field.

		@return Name of the field as String.
	*/
	public String getName() {

		return new String( this.fieldName, 0, nameNullIndex);
	}

	/**
		Returns the data type of the field.

		@return Data type as byte.
	*/
	public byte getDataType() {

		return dataType;
	}

	/**
		Returns field length.

		@return field length as int.
	*/
	public int getFieldLength() {

		return fieldLength;
	}

	/**
		Returns the decimal part. This is applicable
		only if the field type if of numeric in nature.

		If the field is specified to hold integral values
		the value returned by this method will be zero.

		@return decimal field size as int.
	*/
	public int getDecimalCount() {

		return decimalCount;
	}

	// Setter methods

	// byte[] fieldName = new byte[ 11]; /* 0-10*/
  // byte dataType;                    /* 11 */
  // int reserv1;                      /* 12-15 */
  // byte fieldLength;                 /* 16 */
  // byte decimalCount;                /* 17 */
  // short reserv2;                    /* 18-19 */
  // byte workAreaId;                  /* 20 */
  // short reserv3;                    /* 21-22 */
  // byte setFieldsFlag;               /* 23 */
  // byte[] reserv4 = new byte[ 7];    /* 24-30 */
  // byte indexFieldFlag;              /* 31 */

	/**
	 * @deprecated This method is depricated as of version 0.3.3.1 and is replaced by {@link #setName( String)}.
	 */
	public void setFieldName( String value) {

		setName( value);
	}

	/**
		Sets the name of the field.

		@param name of the field as String.
		@since 0.3.3.1
	*/
	public void setName( String name) {

		if( name == null) {

			throw new IllegalArgumentException( "Field name cannot be null");
		}

		if( name.length() == 0 || name.length() > 10) {

			throw new IllegalArgumentException( "Field name should be of length 0-10");
		}

		this.fieldName = name.getBytes();
		this.nameNullIndex = this.fieldName.length;
	}

	/**
		Sets the data type of the field.

		@param type of the field. One of the following:<br>
		C, L, N, F, D, M
	*/
	public void setDataType( byte type) {

		switch( type) {

			case 'D':
				this.fieldLength = 8; /* fall through */
			case 'C':
			case 'L':
			case 'N':
			case 'F':
			case 'M':

				this.dataType = type;
				break;

			default:
				throw new IllegalArgumentException( "Unknown data type");
		}
	}

	/**
		Length of the field.
		This method should be called before calling setDecimalCount().

		@param length of the field as int.
	*/
	public void setFieldLength( int length) {

		if( length <= 0) {

			throw new IllegalArgumentException( "Field length should be a positive number");
		}

		if( this.dataType == FIELD_TYPE_D) {

			throw new UnsupportedOperationException( "Cannot do this on a Date field");
		}

		fieldLength = length;
	}

	/**
		Sets the decimal place size of the field.
		Before calling this method the size of the field
		should be set by calling setFieldLength().

		@param size of the decimal field.
	*/
	public void setDecimalCount( int size) {

		if( size < 0) {

			throw new IllegalArgumentException( "Decimal length should be a positive number");
		}

		if( size > fieldLength) {

			throw new IllegalArgumentException( "Decimal length should be less than field length");
		}

		decimalCount = (byte)size;
	}

}
