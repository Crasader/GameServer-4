// automatically generated by the FlatBuffers compiler, do not modify

package schema;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class PlayerInSession extends Table {
  public static PlayerInSession getRootAsPlayerInSession(ByteBuffer _bb) { return getRootAsPlayerInSession(_bb, new PlayerInSession()); }
  public static PlayerInSession getRootAsPlayerInSession(ByteBuffer _bb, PlayerInSession obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; }
  public PlayerInSession __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public String name() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer nameAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public int amount() { int o = __offset(6); return o != 0 ? bb.getInt(o + bb_pos) : 0; }

  public static int createPlayerInSession(FlatBufferBuilder builder,
      int nameOffset,
      int amount) {
    builder.startObject(2);
    PlayerInSession.addAmount(builder, amount);
    PlayerInSession.addName(builder, nameOffset);
    return PlayerInSession.endPlayerInSession(builder);
  }

  public static void startPlayerInSession(FlatBufferBuilder builder) { builder.startObject(2); }
  public static void addName(FlatBufferBuilder builder, int nameOffset) { builder.addOffset(0, nameOffset, 0); }
  public static void addAmount(FlatBufferBuilder builder, int amount) { builder.addInt(1, amount, 0); }
  public static int endPlayerInSession(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

