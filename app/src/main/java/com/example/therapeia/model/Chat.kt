package com.example.therapeia.model

data class Chat(var sender:String = "",
                var receiver:String = "",
                var message:String = "",
                var isseen: Boolean = false,
                var url:String = "",
                var messageId:String = ""
)