package com.mentionsandroid.mention;


/**
 * Created by ningsuhen on 4/29/16.
 */


class CommentTokens{
    private final String rendered;
    CommentToken[] tokens;
    Comment comment;
    CommentTokens(String[] tokens,Comment comment){
        this.tokens = new CommentToken[tokens.length];
        StringBuilder builder = new StringBuilder();
        for(int i=0; i< tokens.length; i++){
            this.tokens[i] = new CommentToken(tokens[i]);
            builder.append(this.tokens[i].getDisplayText());
        }
        this.rendered = builder.toString();
    }

    String render(){
        return rendered;
    }

    //"hello , [Ningsuhen Waikhom] [Himanshu Goel]"
    // 0       10               30 31            45


    class CommentToken{
        boolean isMention = false;
        String token;
        CommentToken(String token){
            this.token = token;
            if (token.startsWith("@")){
                isMention = true;
            }
        }

        String getDisplayText(){
            if(isMention){
                return getMentionText();
            }
            return token;
        }

        String getMentionText(){
            return CommentTokens.this.comment.getPageById(token.substring(1)).title;
        }
    }
}