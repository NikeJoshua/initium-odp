package com.universeprojects.miniup.server.commands;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.universeprojects.cacheddatastore.CachedDatastoreService;
import com.universeprojects.cacheddatastore.CachedEntity;
import com.universeprojects.miniup.server.NotificationType;
import com.universeprojects.miniup.server.ODPDBAccess;
import com.universeprojects.miniup.server.TradeObject;
import com.universeprojects.miniup.server.commands.framework.Command;
import com.universeprojects.miniup.server.commands.framework.UserErrorMessage;

public class CommandTradeReady extends Command {
	
	public CommandTradeReady(ODPDBAccess db, HttpServletRequest request, HttpServletResponse response)
	{
		super(db, request, response);
	}
	
	public void run(Map<String,String> parameters) throws UserErrorMessage {
		
		
		ODPDBAccess db = getDB();
		CachedDatastoreService ds = getDS();
		Integer version = Integer.parseInt(parameters.get("version"));
		CachedEntity character = db.getCurrentCharacter(request);
		Key otherCharacter = (Key) character.getProperty("combatant");
		
        TradeObject trade = db.setTradeReady(null, db.getCurrentCharacter(request), version);
            if (trade.isComplete()==true)
            {
            	db.sendNotification(ds, otherCharacter, NotificationType.tradeChanged);
                return;
            }
        return;
	}
}