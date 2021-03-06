package com.universeprojects.miniup.server.commands;

import java.util.List;
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

public class CommandTradeAddAllItems extends Command {
	
	public CommandTradeAddAllItems(ODPDBAccess db, HttpServletRequest request, HttpServletResponse response)
	{
		super(db, request, response);
	}
	
	public void run(Map<String,String> parameters) throws UserErrorMessage {
		
		ODPDBAccess db = getDB();
		CachedDatastoreService ds = getDS();
		
		CachedEntity character = db.getCurrentCharacter(request);
		List<CachedEntity> items = db.getFilteredList("Item", "containerKey", character.getKey());
		Key otherCharacter = (Key) character.getProperty("combatant");
        TradeObject tradeObject = TradeObject.getTradeObjectFor(ds, db.getCurrentCharacter(request));
        
        if (tradeObject==null || tradeObject.isCancelled())
        {
            addCallbackData("tradeCancelled", true);
            throw new UserErrorMessage("Trade has been cancelled.");
        }
        
		for(CachedEntity item:items)
		{
			if (db.checkCharacterHasItemEquipped(character, item.getKey()))
                continue;
            
            if (db.checkItemIsVending(character.getKey(), item.getKey()))
                continue;
		}   
        
		db.addTradeItems(ds, db.getCurrentCharacter(request), items);
        db.sendNotification(ds, otherCharacter, NotificationType.tradeChanged);
        
        Integer tradeVersion = tradeObject.getVersion();
        addCallbackData("tradeVersion", tradeVersion);
	}
}