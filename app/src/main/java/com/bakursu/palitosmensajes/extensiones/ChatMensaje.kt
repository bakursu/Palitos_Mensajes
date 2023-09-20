/*
 * realizado por palitodev Jose Rios
 * correo jdrios.c7@gmail.com
 *  21/09/22 12:37
 */

package com.bakursu.palitosmensajes.extensiones



class ChatMensaje(
    val id: String,
    val text: String,
    val fromId: String,
    val toId: String,
    val timestamp: Long
){
    constructor():this ("","","","",-1)
}