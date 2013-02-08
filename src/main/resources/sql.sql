CREATE TABLE IF NOT EXISTS `kills` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `server` varchar(32) NOT NULL,
  `killer` varchar(16) NOT NULL,
  `victim` varchar(16) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `server` (`server`),
  KEY `killer` (`killer`),
  KEY `victim` (`victim`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `sessions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `server` varchar(32) NOT NULL,
  `player` varchar(16) NOT NULL,
  `start` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `duration` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `server` (`server`),
  KEY `player` (`player`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;