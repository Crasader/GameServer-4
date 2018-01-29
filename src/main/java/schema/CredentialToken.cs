// <auto-generated>
//  automatically generated by the FlatBuffers compiler, do not modify
// </auto-generated>

namespace schema
{

using global::System;
using global::FlatBuffers;

public struct CredentialToken : IFlatbufferObject
{
  private Table __p;
  public ByteBuffer ByteBuffer { get { return __p.bb; } }
  public static CredentialToken GetRootAsCredentialToken(ByteBuffer _bb) { return GetRootAsCredentialToken(_bb, new CredentialToken()); }
  public static CredentialToken GetRootAsCredentialToken(ByteBuffer _bb, CredentialToken obj) { return (obj.__assign(_bb.GetInt(_bb.Position) + _bb.Position, _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __p.bb_pos = _i; __p.bb = _bb; }
  public CredentialToken __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public string Token { get { int o = __p.__offset(4); return o != 0 ? __p.__string(o + __p.bb_pos) : null; } }
  public ArraySegment<byte>? GetTokenBytes() { return __p.__vector_as_arraysegment(4); }

  public static Offset<CredentialToken> CreateCredentialToken(FlatBufferBuilder builder,
      StringOffset tokenOffset = default(StringOffset)) {
    builder.StartObject(1);
    CredentialToken.AddToken(builder, tokenOffset);
    return CredentialToken.EndCredentialToken(builder);
  }

  public static void StartCredentialToken(FlatBufferBuilder builder) { builder.StartObject(1); }
  public static void AddToken(FlatBufferBuilder builder, StringOffset tokenOffset) { builder.AddOffset(0, tokenOffset.Value, 0); }
  public static Offset<CredentialToken> EndCredentialToken(FlatBufferBuilder builder) {
    int o = builder.EndObject();
    builder.Required(o, 4);  // token
    return new Offset<CredentialToken>(o);
  }
};


}
