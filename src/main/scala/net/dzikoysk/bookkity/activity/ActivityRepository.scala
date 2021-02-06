package net.dzikoysk.bookkity.activity

import java.io.File
import java.util
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors
import util.{ List => JList, ArrayList => JArrayList, Map => JMap }

import net.dzikoysk.cdn.CDN
import net.dzikoysk.cdn.model.{Configuration, Entry, Section}
import org.panda_lang.utilities.commons.FileUtils

class ActivityRepository {

  private val activitiesFile = new File("activities.cdn").getAbsoluteFile
  private val activities: JMap[String, JList[Long]] = new ConcurrentHashMap[String, JList[Long]]()
  private var closed = false

  def increment(userId: String): Integer = {
    val value = activities.computeIfAbsent(userId, _ => new JArrayList[Long]())
    value.add(System.currentTimeMillis())
    value.size
  }

  def load(): Unit = {
    if (!activitiesFile.exists()) {
      return
    }

    val data = CDN.defaultInstance().parse(FileUtils.getContentOfFile(activitiesFile))

    data.getValue.stream()
      .map(entry => entry.asInstanceOf[Section])
      .forEach(section => {
        val messages = section.getValue.stream
          .map(element => element.asInstanceOf[Entry])
          .map(entry => entry.getValue.toLong)
          .collect(Collectors.toList[Long])

        activities.put(section.getName, messages)
      })
  }

  def save(): Unit = {
    val configuration = new Configuration()
    val defaultDescription = new JArrayList[String](0)

    activities.entrySet().forEach(entry => {
      val values = entry.getValue.stream()
        .map(value => Entry.of(value.toString, defaultDescription))
        .collect(Collectors.toList[Entry])

      configuration.append(new Section(entry.getKey, defaultDescription, values))
    })

    if (!activitiesFile.exists()) {
      activitiesFile.getParentFile.mkdirs()
      activitiesFile.createNewFile()
    }

    FileUtils.overrideFile(activitiesFile, CDN.defaultInstance().compose(configuration))
  }

  def close(): Unit = {
    if (!closed) {
      this.closed = true
      save()
    }
  }

}
