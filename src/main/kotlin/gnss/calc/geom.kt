package gnss.calc

/*
 * Created by aimozg on 22.03.2019.
 * Confidential unless published on GitHub
 */
class Point3D(val xyz: DoubleArray) {
	constructor(x: Double, y: Double, z: Double) : this(doubleArrayOf(x, y, z))


	init {
		if (xyz.size != 3) error("Expected array size 3, got ${xyz.size}")
	}

	var x: Double
		get() = xyz[0]
		set(value) {
			xyz[0] = value
		}
	var y: Double
		get() = xyz[1]
		set(value) {
			xyz[1] = value
		}
	var z: Double
		get() = xyz[2]
		set(value) {
			xyz[2] = value
		}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Point3D

		if (!xyz.contentEquals(other.xyz)) return false

		return true
	}

	override fun hashCode(): Int {
		return xyz.contentHashCode()
	}

	companion object {
		val ZERO = Point3D(0.0, 0.0, 0.0)
	}
}