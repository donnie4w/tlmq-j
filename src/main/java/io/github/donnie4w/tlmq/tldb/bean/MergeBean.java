/**
 * Autogenerated by Thrift Compiler (0.17.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package io.github.donnie4w.tlmq.tldb.bean;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.17.0)", date = "2023-06-17")
public class MergeBean implements org.apache.thrift.TBase<MergeBean, MergeBean._Fields>, java.io.Serializable, Cloneable, Comparable<MergeBean> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("MergeBean");

  private static final org.apache.thrift.protocol.TField BEAN_LIST_FIELD_DESC = new org.apache.thrift.protocol.TField("beanList", org.apache.thrift.protocol.TType.LIST, (short)1);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new MergeBeanStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new MergeBeanTupleSchemeFactory();

  public @org.apache.thrift.annotation.Nullable java.util.List<java.nio.ByteBuffer> beanList; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    BEAN_LIST((short)1, "beanList");

    private static final java.util.Map<String, _Fields> byName = new java.util.HashMap<String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // BEAN_LIST
          return BEAN_LIST;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    @Override
    public short getThriftFieldId() {
      return _thriftId;
    }

    @Override
    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.BEAN_LIST, new org.apache.thrift.meta_data.FieldMetaData("beanList", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING            , true))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(MergeBean.class, metaDataMap);
  }

  public MergeBean() {
  }

  public MergeBean(
    java.util.List<java.nio.ByteBuffer> beanList)
  {
    this();
    this.beanList = beanList;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public MergeBean(MergeBean other) {
    if (other.isSetBeanList()) {
      java.util.List<java.nio.ByteBuffer> __this__beanList = new java.util.ArrayList<java.nio.ByteBuffer>(other.beanList);
      this.beanList = __this__beanList;
    }
  }

  @Override
  public MergeBean deepCopy() {
    return new MergeBean(this);
  }

  @Override
  public void clear() {
    this.beanList = null;
  }

  public int getBeanListSize() {
    return (this.beanList == null) ? 0 : this.beanList.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<java.nio.ByteBuffer> getBeanListIterator() {
    return (this.beanList == null) ? null : this.beanList.iterator();
  }

  public void addToBeanList(java.nio.ByteBuffer elem) {
    if (this.beanList == null) {
      this.beanList = new java.util.ArrayList<java.nio.ByteBuffer>();
    }
    this.beanList.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.List<java.nio.ByteBuffer> getBeanList() {
    return this.beanList;
  }

  public MergeBean setBeanList(@org.apache.thrift.annotation.Nullable java.util.List<java.nio.ByteBuffer> beanList) {
    this.beanList = beanList;
    return this;
  }

  public void unsetBeanList() {
    this.beanList = null;
  }

  /** Returns true if field beanList is set (has been assigned a value) and false otherwise */
  public boolean isSetBeanList() {
    return this.beanList != null;
  }

  public void setBeanListIsSet(boolean value) {
    if (!value) {
      this.beanList = null;
    }
  }

  @Override
  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable Object value) {
    switch (field) {
    case BEAN_LIST:
      if (value == null) {
        unsetBeanList();
      } else {
        setBeanList((java.util.List<java.nio.ByteBuffer>)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  @Override
  public Object getFieldValue(_Fields field) {
    switch (field) {
    case BEAN_LIST:
      return getBeanList();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  @Override
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case BEAN_LIST:
      return isSetBeanList();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that instanceof MergeBean)
      return this.equals((MergeBean)that);
    return false;
  }

  public boolean equals(MergeBean that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_beanList = true && this.isSetBeanList();
    boolean that_present_beanList = true && that.isSetBeanList();
    if (this_present_beanList || that_present_beanList) {
      if (!(this_present_beanList && that_present_beanList))
        return false;
      if (!this.beanList.equals(that.beanList))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetBeanList()) ? 131071 : 524287);
    if (isSetBeanList())
      hashCode = hashCode * 8191 + beanList.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(MergeBean other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.compare(isSetBeanList(), other.isSetBeanList());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetBeanList()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.beanList, other.beanList);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  @Override
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  @Override
  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  @Override
  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("MergeBean(");
    boolean first = true;

    sb.append("beanList:");
    if (this.beanList == null) {
      sb.append("null");
    } else {
      org.apache.thrift.TBaseHelper.toString(this.beanList, sb);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (beanList == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'beanList' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class MergeBeanStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    @Override
    public MergeBeanStandardScheme getScheme() {
      return new MergeBeanStandardScheme();
    }
  }

  private static class MergeBeanStandardScheme extends org.apache.thrift.scheme.StandardScheme<MergeBean> {

    @Override
    public void read(org.apache.thrift.protocol.TProtocol iprot, MergeBean struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // BEAN_LIST
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
                struct.beanList = new java.util.ArrayList<java.nio.ByteBuffer>(_list0.size);
                @org.apache.thrift.annotation.Nullable java.nio.ByteBuffer _elem1;
                for (int _i2 = 0; _i2 < _list0.size; ++_i2)
                {
                  _elem1 = iprot.readBinary();
                  struct.beanList.add(_elem1);
                }
                iprot.readListEnd();
              }
              struct.setBeanListIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    @Override
    public void write(org.apache.thrift.protocol.TProtocol oprot, MergeBean struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.beanList != null) {
        oprot.writeFieldBegin(BEAN_LIST_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, struct.beanList.size()));
          for (java.nio.ByteBuffer _iter3 : struct.beanList)
          {
            oprot.writeBinary(_iter3);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class MergeBeanTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    @Override
    public MergeBeanTupleScheme getScheme() {
      return new MergeBeanTupleScheme();
    }
  }

  private static class MergeBeanTupleScheme extends org.apache.thrift.scheme.TupleScheme<MergeBean> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, MergeBean struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      {
        oprot.writeI32(struct.beanList.size());
        for (java.nio.ByteBuffer _iter4 : struct.beanList)
        {
          oprot.writeBinary(_iter4);
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, MergeBean struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      {
        org.apache.thrift.protocol.TList _list5 = iprot.readListBegin(org.apache.thrift.protocol.TType.STRING);
        struct.beanList = new java.util.ArrayList<java.nio.ByteBuffer>(_list5.size);
        @org.apache.thrift.annotation.Nullable java.nio.ByteBuffer _elem6;
        for (int _i7 = 0; _i7 < _list5.size; ++_i7)
        {
          _elem6 = iprot.readBinary();
          struct.beanList.add(_elem6);
        }
      }
      struct.setBeanListIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}
