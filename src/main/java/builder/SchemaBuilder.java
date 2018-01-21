package builder;

import Schema.CredentialToken;
import Schema.Data;
import Schema.Message;
import Schema.ReconnectKey;
import com.google.flatbuffers.FlatBufferBuilder;

public class SchemaBuilder {
    public static FlatBufferBuilder buildCredentialToken(String token) {
        FlatBufferBuilder builder = new FlatBufferBuilder(1);
        int tokenId = builder.createString(token);
        CredentialToken.startCredentialToken(builder);
        CredentialToken.addToken(builder, tokenId);
        int cred = CredentialToken.endCredentialToken(builder);
        return buildMessage(builder, cred, Data.CredentialToken);
    }

    public static FlatBufferBuilder buildReconnectKey(String authKey) {
        FlatBufferBuilder builder = new FlatBufferBuilder(1);
        int  key = builder.createString(authKey);
        ReconnectKey.startReconnectKey(builder);
        ReconnectKey.addKey(builder, key);
        int reconnectKey = ReconnectKey.endReconnectKey(builder);
        return buildMessage(builder, reconnectKey, Data.ReconnectKey);
    }

    public static FlatBufferBuilder buildMessage(FlatBufferBuilder builder, int data, byte dataType) {
        Message.startMessage(builder);
        Message.addDataType(builder, dataType);
        Message.addData(builder, data);
        int finalData = Message.endMessage(builder);
        builder.finish(finalData);
        return builder;
    }
}
