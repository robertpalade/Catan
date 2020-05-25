﻿using System.Collections;
using System.Collections.Generic;
using System.Threading;
using UnityEngine.UI;
using UnityEditor;
using UnityEngine;
using FullSerializer;
using Proyecto26;
using UnityEngine.SceneManagement;
using System.Text;
using SocketIO;
using System;
using System.Runtime.InteropServices;
using System.Threading;

public class AcceptTrade : MonoBehaviour
{

    public Text lumber;
    public Text ore;
    public Text brick;
    public Text grain;
    public Text wool;
    public SocketIOComponent socket;

    public void acceptTrade()
    {
        MakeRequest.acceptTrade(LoginScript.CurrentUserGameId, LoginScript.CurrentUserGEId);
        Thread.Sleep(2000);
        updateResource();
    }
    
    public void updateResource()
    {
        GameObject go = GameObject.Find("SocketIO");
        socket = go.GetComponent<SocketIOComponent>();
        MakeRequestResponse command1 = new MakeRequestResponse();
        command1.gameId = LoginScript.CurrentUserGameId;
        command1.playerId = LoginScript.CurrentUserGEId;
        RequestJson req1 = new RequestJson();
        RestClient.Post<UpdateJson>("https://catan-connectivity.herokuapp.com/game/update", command1).Then(Response1 =>
        {
            Debug.Log("Update code " + Response1.code);
            Debug.Log("Update status " + Response1.status);
            Debug.Log("Update arguments lumber " + Response1.arguments.lumber);
            Debug.Log("Update arguments settle " + Response1.arguments.settlements[1]);
            // Debug.Log("Update roads " + Response.arguments.roads[0][1]);//NU MERGE ASTA
            // Debug.Log("Update roads " + Response.arguments.roads[0][0]);//NU MERGE NICI ASTA

            lumber.text = Response1.arguments.lumber.ToString();
            ore.text = Response1.arguments.ore.ToString();
            grain.text = Response1.arguments.grain.ToString();
            brick.text = Response1.arguments.brick.ToString();
            wool.text = Response1.arguments.wool.ToString();

        }).Catch(err => { Debug.Log(err); });
    }
}
